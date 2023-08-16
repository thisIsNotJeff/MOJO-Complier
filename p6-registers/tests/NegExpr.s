	.text
NegExpr.Neg:
	pushq %rbp
	movq %rsp,%rbp
#	movq %rdi,%rdi
L.3:
	xorq %rax,%rax
#	movq %rax,%rax
	subq %rdi,%rax
#	movq %rax,%rax
L.0:
#	returnSink
	popq %rbp
	ret
	.text
NegExpr:
	pushq %rbp
	movq %rsp,%rbp
#	returnSink
	popq %rbp
	ret
	.globl main
	.text
main:
	pushq %rbp
	movq %rsp,%rbp
L.4:
	call NegExpr
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.1:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
	pushq %rbp
	movq %rsp,%rbp
L.5:
	leaq L.1(%rip),%rdi
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
L.2:	.asciz	"Subscript out of bounds"
	.text
badSub:
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
