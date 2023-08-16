	.text
LocalArrayFixed2d:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %rbx, -8(%rbp)
L.6:
	movabsq $400,%rdi
#	movq %rdi,%rdi
	call malloc
#	movq %rax,%rax
	movq $10,%r8
	xorq %rsi,%rsi
	cmpq %r8,%rsi
	jge L.1
L.0:
	movq $5,%rdi
	xorq %rbx,%rbx
	cmpq %rdi,%rbx
	jge L.3
L.2:
	movq %rax,%rdx
	movq %rsi,%rcx
	imulq $40,%rcx
	addq %rcx,%rdx
#	movq %rdx,%rdx
	movq %rbx,%rcx
	shlq $3,%rcx
	addq %rcx,%rdx
	movq $0,0(%rdx)
#	movq %rbx,%rbx
	addq $1,%rbx
#	movq %rbx,%rbx
	cmpq %rdi,%rbx
	jl L.2
L.3:
#	movq %rsi,%rsi
	addq $1,%rsi
#	movq %rsi,%rsi
	cmpq %r8,%rsi
	jl L.0
L.1:
#	movq %rax,%rax
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
	leaq L.4(%rip),%rdi
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
L.5:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.9:
	leaq L.5(%rip),%rdi
#	movq %rdi,%rdi
	call puts
	movabsq $1,%rdi
#	movq %rdi,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
