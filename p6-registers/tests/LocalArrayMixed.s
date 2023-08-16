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
#	movq %r12,%r12
	movq $16,%rdi
#	movq %rdi,%rdi
	movq %r12,%rax
	imulq $40,%rax
	addq %rax,%rdi
#	movq %rdi,%rdi
#	movq %rdi,%rdi
	call malloc
#	movq %rax,%rax
	movq %rax,%rbx
	addq $16,%rbx
	movq %rbx,0(%rax)
	movq %r12,8(%rax)
	movq 8(%rax),%rcx
#	movq %rcx,%rcx
	movq 0(%rax),%rbx
#	movq %rbx,%rbx
	xorq %r8,%r8
	cmpq %rcx,%r8
	jge L.4
L.2:
	movq $5,%r9
	xorq %rdx,%rdx
	cmpq %r9,%rdx
	jge L.6
L.5:
	movq %rbx,%rdi
	movq %r8,%rsi
	imulq $40,%rsi
	addq %rsi,%rdi
#	movq %rdi,%rdi
	movq %rdx,%rsi
	shlq $3,%rsi
	addq %rsi,%rdi
	movq $0,0(%rdi)
#	movq %rdx,%rdx
	addq $1,%rdx
#	movq %rdx,%rdx
	cmpq %r9,%rdx
	jl L.5
L.6:
#	movq %r8,%r8
	addq $1,%r8
#	movq %r8,%r8
L.3:
	cmpq %rcx,%r8
	jl L.2
L.4:
#	movq %rax,%rax
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
	leaq L.7(%rip),%rdi
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
L.8:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.12:
	leaq L.8(%rip),%rdi
#	movq %rdi,%rdi
	call puts
	movabsq $1,%rdi
#	movq %rdi,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
