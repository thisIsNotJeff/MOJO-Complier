	.text
LocalArrayOpen:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %r12, -16(%rbp)
	movq %rbx, -8(%rbp)
L.7:
	movq $10,%r12
	xorq %rax,%rax
	cmpq %rax,%r12
	jl L.0
L.1:
	movq %r12,%rax
	movq $16,%rbx
#	movq %rbx,%rbx
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rbx
#	movq %rbx,%rbx
	movq %rbx,%rdi
	call malloc
	movq %rax,%rsi
	movq %rsi,%rax
	addq $16,%rax
	movq %rax,0(%rsi)
	movq %r12,8(%rsi)
	movq 8(%rsi),%rax
	movq %rax,%rdi
	movq 0(%rsi),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rdi,%rbx
	jge L.4
L.2:
	movq %rax,%rcx
	movq %rbx,%rdx
	shlq $3,%rdx
	addq %rdx,%rcx
	movq $0,0(%rcx)
#	movq %rbx,%rbx
	addq $1,%rbx
#	movq %rbx,%rbx
L.3:
	cmpq %rdi,%rbx
	jl L.2
L.4:
	movq %rsi,%rax
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
L.8:
	call LocalArrayOpen
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.5:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
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
	.data
	.balign 8
L.6:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.10:
	leaq L.6(%rip),%rax
	movq %rax,%rdi
	call puts
	movabsq $1,%rax
	movq %rax,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
