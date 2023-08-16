	.text
LocalArrayFixed2d:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %rbx, -8(%rbp)
L.6:
	movabsq $400,%rax
	movq %rax,%rdi
	call malloc
	movq %rax,%rsi
	movq $10,%rcx
	xorq %r8,%r8
	cmpq %rcx,%r8
	jge L.1
L.0:
	movq $5,%rdx
	xorq %rdi,%rdi
	cmpq %rdx,%rdi
	jge L.3
L.2:
	movq %rsi,%rax
	movq %r8,%rbx
	imulq $40,%rbx
	addq %rbx,%rax
#	movq %rax,%rax
	movq %rdi,%rbx
	shlq $3,%rbx
	addq %rbx,%rax
	movq $0,0(%rax)
	movq %rdi,%rax
	addq $1,%rax
	movq %rax,%rdi
	cmpq %rdx,%rdi
	jl L.2
L.3:
	movq %r8,%rax
	addq $1,%rax
	movq %rax,%r8
	cmpq %rcx,%r8
	jl L.0
L.1:
	movq %rsi,%rax
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
L.7:
	call LocalArrayFixed2d
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.4:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
	pushq %rbp
	movq %rsp,%rbp
L.8:
	leaq L.4(%rip),%rax
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
L.5:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.9:
	leaq L.5(%rip),%rax
	movq %rax,%rdi
	call puts
	movabsq $1,%rax
	movq %rax,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
