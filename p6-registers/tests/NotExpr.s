	.text
NotExpr.Not:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %rbx, -8(%rbp)
#	movq %rdi,%rdi
L.5:
	xorq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rdi
	je L.1
L.2:
#	movq %rax,%rax
	jmp L.0
L.1:
	movq $1,%rax
	jmp L.2
L.0:
#	returnSink
	movq -8(%rbp),%rbx
	addq $16,%rsp
	popq %rbp
	ret
	.text
NotExpr:
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
L.6:
	call NotExpr
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.3:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
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
	.data
	.balign 8
L.4:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.8:
	leaq L.4(%rip),%rdi
#	movq %rdi,%rdi
	call puts
	movabsq $1,%rdi
#	movq %rdi,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
