	.data
	.balign 8
L.0:	.asciz	"Number is: %d\n"
	.text
PrintInt:
	pushq %rbp
	movq %rsp,%rbp
L.4:
	leaq L.0(%rip),%rdi
#	movq %rdi,%rdi
	movabsq $42,%rsi
#	movq %rsi,%rsi
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
	leaq L.2(%rip),%rdi
#	movq %rdi,%rdi
	call puts
	movabsq $1,%rdi
#	movq %rdi,%rdi
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
	leaq L.3(%rip),%rdi
#	movq %rdi,%rdi
	call puts
	movabsq $1,%rdi
#	movq %rdi,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
