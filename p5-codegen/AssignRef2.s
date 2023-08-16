	.text
AssignRef2:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %rbx, -8(%rbp)
L.2:
	movabsq $8,%rax
	movq %rax,%rdi
	call malloc
	movq %rax,%rbx
	movabsq $0,%rax
	movq %rax,0(%rbx)
	movq %rbx,%rax
#	returnSink
	movq -8(%rbp),%rbx
	addq $16,%rsp
	popq %rbp
	ret
	.globl main
	.text
main:
	pushq %rbp
	movq %rsp,%rbp
L.3:
	call AssignRef2
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.0:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
	pushq %rbp
	movq %rsp,%rbp
L.4:
	leaq L.0(%rip),%rax
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
L.1:	.asciz	"Subscript out of bounds"
	.text
badSub:
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
