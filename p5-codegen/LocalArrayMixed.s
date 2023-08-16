	.text
LocalArrayMixed:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %r12, -16(%rbp)
	movq %rbx, -8(%rbp)
L.9:
	movabsq $10,%rcx
	movq %rcx,%r12
	cmpq $0,%r12
	jl L.0
L.1:
	movq %r12,%rdx
	movabsq $16,%rcx
#	movq %rcx,%rcx
	movq $40,%rax
	imulq %rax, %rdx
	addq %rdx, %rcx
#	movq %rcx,%rcx
	movq %rcx,%rdi
	call malloc
	movq %rax,%r9
	addq $16,%r9
	movq %r9,0(%r9)
	movq %r12,8(%r9)
	movq 8(%r9),%rax
	movq %rax,%r8
	movq 0(%r9),%rax
	movq %rax,%rdi
	movabsq $0,%rax
	movq %rax,%rsi
	cmpq %r8,%rsi
	jge L.4
L.2:
	movabsq $5,%rax
	movq %rax,%rdx
	movabsq $0,%rax
	movq %rax,%rcx
	cmpq %rdx,%rcx
	jge L.6
L.5:
	shlq $3,%rcx
	movq $40,%rbx
	imulq %rbx, %rsi
	addq %rsi, %rdi
	addq %rcx, %rdi
	movabsq $0,%rax
	movq %rax,0(%rdi)
	addq $1,%rcx
#	movq %rcx,%rcx
	cmpq %rdx,%rcx
	jl L.5
L.6:
	addq $1,%rsi
#	movq %rsi,%rsi
L.3:
	cmpq %r8,%rsi
	jl L.2
L.4:
	movq %r9,%rax
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
