#!/bin/bash

shopt -s extglob 

#######################################
#           Peter Oslington           #
#      peter.oslington@anu.edu.au     #
#######################################



#######################################
# Argument Parsing                    #
#######################################

# Bash array of tests
TESTS=()

# Parse arguments
for arg in "$@"; do

    ### Bunch of build options

    # Make from scratch source files
    if [ "$arg" = "--make-clean" ]; then
        make clean && make all

        if [ "$?" != 0 ]; then
        echo "0 / 0 passed. (make failed)"
        exit 1
        fi

    # Run make before tests
    elif [ "$arg" = "--make" ]; then
        make build

        if [ "$?" != 0 ]; then
        echo "0 / 0 passed. (make failed)"
        exit 1
        fi

    # Clean up all tests and exit
    # Will remove all .s files in the top level directory
    elif [ "$arg" = "--clean-only" ]; then
        rm -r output 2> /dev/null
        rm *.s 2> /dev/null
        rm tests/*.s 2> /dev/null
        rm tests/*.elf 2> /dev/null
        rm tests/*.elf.err 2> /dev/null
        rm tests/+([^.]).out  2> /dev/null
        exit 0
    
    # Clean up all tests before running
    # Will remove all .s files in the top level directory
    elif [ "$arg" = "--clean" ]; then
        rm -r output 2> /dev/null
        rm *.s 2> /dev/null
        rm tests/*.s 2> /dev/null
        rm tests/*.elf 2> /dev/null
        rm tests/*.elf.err 2> /dev/null
        rm tests/+([^.]).out  2> /dev/null
    
    ### Output formatting

    # Add colour output
    elif [ "$arg" = "--color" ]; then
        RED='[0;31m'
        GREEN='[0;32m'
        NORM='[0;00m'

    ### Testing behaviour

    # Enable output test (defaults to enabled)
    elif [ "$arg" = "--enable-out-test" ]; then
        RUN_OUTPUT_TEST=1
    
    # Disable output test
    elif [ "$arg" = "--disable-out-test" ]; then
        RUN_OUTPUT_TEST=0

    # Enable error test (defaults to enabled)
    elif [ "$arg" = "--enable-err-test" ]; then
        RUN_ERROR_TEST=1
    
    # Disable error test
    elif [ "$arg" = "--disable-err-test" ]; then
        RUN_ERROR_TEST=0

    # Enable assembly test (defaults to enabled)
    elif [ "$arg" = "--enable-asm-test" ]; then
        RUN_ASM_TEST=1
    
    # Disable assembly test
    elif [ "$arg" = "--disable-asm-test" ]; then
        RUN_ASM_TEST=0

    # Enable building assembly
    elif [ "$arg" = "--build-asm" ]; then
        BUILD_ASM=1

    # Enable binary output test
    elif [ "$arg" = "--enable-bin-test" ]; then
        RUN_BIN_TEST=1
    
    # Disable binary output test (defaults to disabled)
    elif [ "$arg" = "--disable-bin-test" ]; then
        RUN_BIN_TEST=0

    # Use tony's error sed behaviour (probably a bad idea)
    elif [ "$arg" = "--tony-error-sed" ]; then
        PRINT_OUTPUT_DIFF=1

    ### Printing behaviour

    # Dump output diff after test
    elif [ "$arg" = "--show-out-diff" ]; then
        PRINT_OUTPUT_DIFF=1

    # Dump error diff after test
    elif [ "$arg" = "--show-err-diff" ]; then
        PRINT_ERROR_DIFF=1

    # Dump difference in assembly files
    elif [ "$arg" = "--show-asm-diff" ]; then
        PRINT_ASM_DIFF=1

    # Dump info about if builds failed (default)
    elif [ "$arg" = "--show-build-problems" ]; then
        PRINT_ASM_ISSUES=1
    
    # Hide appended info about builds
    elif [ "$arg" = "--hide-build-problems" ]; then
        PRINT_ASM_ISSUES=0
    
    # Else assume is test name
    else
        if [[ ${arg:0:1} == "-" ]]; then
            echo "Unrecognized option: '$arg'"
            exit 1
        else
          TESTS+=("$arg")
        fi
    fi
done

#######################################
# Define a bunch of testing functions #
#######################################

# Actually run the test, outputting to the normal folders
function run_test() {

    # Test name is first argument
    test_name=$1

    # Output files
    out=output/${t//\//_}.out # output file (no slashes)
    err=output/${t//\//_}.err # stderr file (no slashes)

    $TIMEOUT java -cp bin mojo.Main -main $t 2> $err > $out

}

# Compare the output files to the expected values
function check_output() {

    # Test name is first argument
    test_name=$1

    expout=$t.out             # expected stdout
    out=output/${t//\//_}.out # output file (no slashes)
    outdiff=$out.diff

    diff $expout $out 2>/dev/null > $outdiff


    if [ -s "$outdiff" ]; then
        # Print errors
        if [ $PRINT_OUTPUT_DIFF == 1 ]; then
            echo -e "\nOutput Diff":
            cat $outdiff
            echo
        fi
        return 1
    else    
        return 0
    fi

}

# Compare the error files to the expected values
function check_error() {

    # Test name is first argument
    test_name=$1
    
    experr=$t.err             # expected stderr
    err=output/${t//\//_}.err # stderr file (no slashes)
    errdiff="$err.diff"

    # Use an exact approach of diffing the error files
    if [ $SED_ERRORS == 0 ]; then

        diff $experr $err 2>/dev/null > $errdiff

        if [ -s "$errdiff" ]; then
            # Print errors
            if [ $PRINT_ERROR_DIFF == 1 ]; then
                echo -e "\nError Diff:"
                cat $errdiff
                echo
            fi
            return 1
        else
            return 0
        fi


    # Use Tony's approach of searching for first error
    else
        # Blatently stolen from his script - can't be bothered understanding this enough to rewrite it
        errpat=$(head -n 1 $experr | sed "-es/column\ [0-9]*/column/g")
        sed "-es/column\ [0-9]*/column/g" $err > $err.nocol
        grep -F "$errpat" $err.nocol > /dev/null
        if [ "$?" != "0" ]; then
            return 1
        else
            return 1
        fi


    fi

}

# Extract just the assembly code from Tony's output files
function generate_expected_asm() {

    # Test name is first argument
    test_name=$1

    expout=$test_name.out
    expasm=${test_name%.*}.s

    # Get global variable decls
    sed '/PROCEDURE/Q' $expout > $expasm
    # General case for each asm block
    awk '/(Assembly code|END)/{flag=1;next}/(END|PROCEDURE)/{flag=0}flag' $expout >> $expasm

}

# Compare generate assembly code to expected
function check_asm() {

    # Test name is first argument
    test_name=$1

    expasm=${test_name%.*}.s
    basename=$(basename $test_name)
    asm="${ASM_PATH_PREFIX}${basename%.*}.s"
    output_path="output/${t//\//_}"
    asmdiff=${output_path%.*}.s.diff

    diff $expasm $asm 2>/dev/null > $asmdiff

    if [ -s "$asmdiff" ]; then
        # Print assembly differences
        if [ $PRINT_ASM_DIFF == 1 ]; then
            echo -e "\nAssembly Diff":
            cat $asmdiff
            echo
        fi
        return 1
    else    
        return 0
    fi

}

# Build extracted expected asm and given asm
function build_asm() {

    # Test name is first argument
    test_name=$1

    basename=$(basename $test_name)
    respath=output/${t//\//_}

    expasm=${test_name%.*}.s
    expbin=${test_name%.*}.elf
    expbuilderr=${test_name%.*}.elf.err

    asm="${ASM_PATH_PREFIX}${basename%.*}.s"
    bin=${respath%.*}.elf
    builderr=${respath%.*}.elf.err

    # Build given solution
    gcc -o $expbin $expasm 2> $expbuilderr
    if [ -s $expbuilderr ]; then
        if [ $PRINT_ASM_ISSUES == 1 ]; then
            printf ' - Expected build failed, skipping build'
        fi
        return 1
    fi

    gcc -o $bin $asm 2> $builderr
    if [ -s $builderr ]; then
        if [ $PRINT_ASM_ISSUES == 1 ]; then
            printf ' - Your build failed!'
        fi
        return 1
    fi

    return 0
}

function compare_bin_output() {

    # Test name is first argument
    test_name=$1

    basename=$(basename $test_name)
    respath=output/${t//\//_}

    expasm=${test_name%.*}.s
    expbin=${test_name%.*}.elf
    expbuilderr=${test_name%.*}.elf.err
    expres=${test_name%.*}.out
    experr=${test_name%.*}.err

    asm="${ASM_PATH_PREFIX}${basename%.*}.s"
    bin=${respath%.*}.elf
    builderr=${respath%.*}.elf.err
    res=${respath%.*}.out
    err=${respath%.*}.err

    diff=${respath%.*}.diff


    # Skip if build errors occured
    if [[ -s $expbuilderr || -s $build_err ]]; then
        return 1
    fi

    if [ -f $expbin ]; then
        { $expbin; } &> $expres
    fi
    if [ -f $bin ]; then
        { $bin; } >& $res
    fi

    if [[ -f $expres && -f $res ]]; then
        diff $expres $res > $diff
    fi

    if [ -s $diff ]; then
        if [ $PRINT_BUILD_DIFF == 1 ]; then
            echo -e "\nBuild Diff":
            cat $diff
            echo
        fi
        return 1
    fi

    return 0

} 


#######################################
# Variable setup                      #
#######################################


# Setup for tests
mkdir -p output/

# Set default tests to all
if [ ${#TESTS[@]} == 0 ]; then
    TESTS=(tests/*.mj)
fi

# Use timeout if present
TIMEOUT=$(which timeout)
if [ "$?" = "0" ]; then
    TIMEOUT="timeout 10"
else
    TIMEOUT=""
fi

# Default path for the asm file - can be fixed at ln 130 of src/Mojo/main.java
ASM_PATH_PREFIX="./output/tests_"

# During test print behaviour
PRINT_OUTPUT_DIFF=${PRINT_OUTPUT_DIFF:=0}
PRINT_ERROR_DIFF=${PRINT_ERROR_DIFF:=0}
PRINT_ASM_DIFF=${PRINT_ASM_DIFF:=0}
PRINT_ASM_ISSUES=${PRINT_ASM_ISSUES:=1}
PRINT_BUILD_DIFF=${PRINT_BUILD_DIFF:=0}

# Tests to run
RUN_OUTPUT_TEST=${RUN_OUTPUT_TEST:=1}
RUN_ERROR_TEST=${RUN_ERROR_TEST:=1}
RUN_ASM_TEST=${RUN_ASM_TEST:=1}
BUILD_ASM=${BUILD_ASM:=0}
RUN_BIN_TEST=${RUN_BIN_TEST:=0}

# Testing behaviour
SED_ERRORS=${SED_ERRORS:=0}

# Init counts
passed=0
failed=0
bonus=0
total=${#TESTS[@]}

#######################################
# Main test loop                      #
#######################################

for t in ${TESTS[@]}; do

    # Print running message
    printf "run  $t"

    # Expected output files
    expout=$t.out             # expected stdout
    experr=$t.err             # expected stderr
    
    # Generate output files
    run_test $t

    if [ $RUN_OUTPUT_TEST == 1 ]; then
        check_output $t
        out_res=$?
    else
        out_res=0
    fi

    if [ $RUN_ERROR_TEST == 1 ]; then
        check_error $t
        err_res=$?
    else
        err_res=0
    fi

    if [ $RUN_ASM_TEST == 1 ]; then
        generate_expected_asm $t
        check_asm $t
        asm_res=$?
    else
        asm_res=0
    fi

    if [ $BUILD_ASM == 1 ]; then
        build_asm $t
    fi

    if [ $RUN_BIN_TEST == 1 ]; then
        compare_bin_output $t
        build_res=$?
    else
        build_res=0
    fi

    if [[ $out_res == 0 && $err_res == 0 && $asm_res == 0 && build_res==0 ]]; then
        passed=$(($passed + 1))
        if [[ $t =~ "xc_" ]]; then
            bonus=$(($bonus + 1))
        fi
        printf "\r${GREEN}ok   ${NORM}\n"
    else
        printf "\r${RED}fail ${NORM}\n"
    fi


done

printf "%d / %d passed %d bonus\n" $passed $total $bonus