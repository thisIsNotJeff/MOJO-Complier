	.text
LocalArrayMixed:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %rbx, -8(%rbp)
L.9:
	movq $10,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	jl L.0
L.1:
#	movq %rbx,%rbx
	movq $16,%rax
	movq %rax,%rcx
	movq %rbx,%rax
	imulq $40,%rax
	addq %rax,%rcx
	movq %rcx,%rax
	movq %rax,%rdi
	call malloc
#	movq %rax,%rax
	movq %rax,%rcx
	addq $16,%rcx
	movq %rcx,0(%rax)
	movq %rbx,8(%rax)
	movq 8(%rax),%rbx
	movq %rbx,%r9
	movq 0(%rax),%rbx
	movq %rbx,%r8
	xorq %rdi,%rdi
	cmpq %r9,%rdi
	jge L.4
L.2:
	movq $5,%rsi
	xorq %rdx,%rdx
	cmpq %rsi,%rdx
	jge L.6
L.5:
	movq %r8,%rcx
	movq %rdi,%rbx
	imulq $40,%rbx
	addq %rbx,%rcx
#	movq %rcx,%rcx
	movq %rdx,%rbx
	shlq $3,%rbx
	addq %rbx,%rcx
	movq $0,0(%rcx)
	movq %rdx,%rbx
	addq $1,%rbx
	movq %rbx,%rdx
	cmpq %rsi,%rdx
	jl L.5
L.6:
	movq %rdi,%rbx
	addq $1,%rbx
	movq %rbx,%rdi
L.3:
	cmpq %r9,%rdi
	jl L.2
L.4:
#	movq %rax,%rax
#	returnSink
	movq -8(%rbp),%rbx
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
