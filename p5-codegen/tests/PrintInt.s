	.data
	.balign 8
L.0:	.asciz	"Number is: %d\n"
	.text
PrintInt:
	pushq %rbp
	movq %rsp,%rbp
L.4:
	leaq L.0(%rip),%rax
	movq %rax,%rdi
	movabsq $42,%rax
	movq %rax,%rsi
	call printf
#	returnSink
	popq %rbp
	ret
L.1:
	call badSub
	.globl main
	.text
main:
	pushq %rbp
	movq %rsp,%rbp
L.5:
	call PrintInt
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.2:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
	pushq %rbp
	movq %rsp,%rbp
L.6:
	leaq L.2(%rip),%rax
	movq %rax,%rdi
	call puts
	movabsq $1,%rax
	movq %rax,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.3:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.7:
	leaq L.3(%rip),%rax
	movq %rax,%rdi
	call puts
	movabsq $1,%rax
	movq %rax,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
