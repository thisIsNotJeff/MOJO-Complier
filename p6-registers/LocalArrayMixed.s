	.text
LocalArrayMixed:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %r12, -16(%rbp)
	movq %rbx, -8(%rbp)
L.9:
	movq $10,%r12
	xorq %rax,%rax
	cmpq %rax,%r12
	jl L.0
L.1:
	movq %r12,%rax
	movq $16,%rbx
#	movq %rbx,%rbx
#	movq %rax,%rax
	imulq $40,%rax
	addq %rax,%rbx
#	movq %rbx,%rbx
	movq %rbx,%rdi
	call malloc
	movq %rax,%rbx
	movq %rbx,%rax
	addq $16,%rax
	movq %rax,0(%rbx)
	movq %r12,8(%rbx)
	movq 8(%rbx),%rax
	movq %rax,%rdx
	movq 0(%rbx),%rax
	movq %rax,%rsi
	xorq %r9,%r9
	cmpq %rdx,%r9
	jge L.4
L.2:
	movq $5,%rdi
	xorq %r8,%r8
	cmpq %rdi,%r8
	jge L.6
L.5:
	movq %rsi,%rax
	movq %r9,%rcx
	imulq $40,%rcx
	addq %rcx,%rax
#	movq %rax,%rax
	movq %r8,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $0,0(%rax)
	movq %r8,%rax
	addq $1,%rax
	movq %rax,%r8
	cmpq %rdi,%r8
	jl L.5
L.6:
	movq %r9,%rax
	addq $1,%rax
	movq %rax,%r9
L.3:
	cmpq %rdx,%r9
	jl L.2
L.4:
	movq %rbx,%rax
#	returnSink
	movq -8(%rbp),%rbx
	movq -16(%rbp),%r12
	addq $16,%rsp
	popq %rbp
	ret
L.0:
	call badSub
	.globl main
	.text
main:
	pushq %rbp
	movq %rsp,%rbp
L.10:
	call LocalArrayMixed
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.7:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
	pushq %rbp
	movq %rsp,%rbp
L.11:
	leaq L.7(%rip),%rax
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
L.8:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.12:
	leaq L.8(%rip),%rax
	movq %rax,%rdi
	call puts
	movabsq $1,%rax
	movq %rax,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
