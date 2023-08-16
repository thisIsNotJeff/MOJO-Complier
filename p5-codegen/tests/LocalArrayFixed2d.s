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
	movq %rax,%r8
	movq $10,%rdi
	xorq %rsi,%rsi
	cmpq %rdi,%rsi
	jge L.1
L.0:
	movq $5,%rdx
	xorq %rcx,%rcx
	cmpq %rdx,%rcx
	jge L.3
L.2:
	movq %r8,%rbx
	movq %rsi,%rax
	imulq $40,%rax
	addq %rax,%rbx
#	movq %rbx,%rbx
	movq %rcx,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq $0,0(%rbx)
	movq %rcx,%rax
	addq $1,%rax
	movq %rax,%rcx
	cmpq %rdx,%rcx
	jl L.2
L.3:
	movq %rsi,%rax
	addq $1,%rax
	movq %rax,%rsi
	cmpq %rdi,%rsi
	jl L.0
L.1:
	movq %r8,%rax
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
