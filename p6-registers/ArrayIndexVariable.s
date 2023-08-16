	.data
	.balign 8
ArrayIndexVariable.x:
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.text
ArrayIndexVariable.Get:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %rbx, -8(%rbp)
	movq %rdi,%rax
L.3:
	leaq ArrayIndexVariable.x(%rip),%rbx
#	movq %rbx,%rbx
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq 0(%rbx),%rax
#	movq %rax,%rax
L.0:
#	returnSink
	movq -8(%rbp),%rbx
	addq $16,%rsp
	popq %rbp
	ret
	.text
ArrayIndexVariable:
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
	call ArrayIndexVariable
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
	leaq L.1(%rip),%rax
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
L.2:	.asciz	"Subscript out of bounds"
	.text
badSub:
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
