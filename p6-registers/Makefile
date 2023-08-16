.PHONY: all prep build clean
all:	build
prep:
	javacc -OUTPUT_DIRECTORY="lib/mojo/parse" src/mojo/Parser.jj
build:
	javac -cp bin -d bin src/*/*.java
clean:
	rm -rf bin/Assem bin/Canon bin/Mips bin/Translate bin/interp bin/RegAlloc
	rm -rf output
	find . -name "*~" -exec rm {} \;

MJ = java -cp bin mojo.Main -main
CC = gcc
# TARGET = x64
## Uncomment line above and comment out the following two lines for x64 Linux
TARGET = x64osx
CFLAGS = --target=x86_64-apple-macos

%.s:	tests/%.mj
	@echo "Compiling mj file to s file"
	$(MJ) -target=$(TARGET) $<
%:	%.s
	$(CC) $(CFLAGS) -o $@ $<
