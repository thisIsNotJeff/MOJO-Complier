	.text
LocalArrayOpen:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %rbx, -8(%rbp)
L.7:
	movq $10,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	jl L.0
L.1:
#	movq %rbx,%rbx
	movq $16,%rax
	movq %rax,%rcx
	movq %rbx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq %rcx,%rax
	movq %rax,%rdi
	call malloc
	movq %rax,%rdi
	movq %rdi,%rax
	addq $16,%rax
	movq %rax,0(%rdi)
	movq %rbx,8(%rdi)
	movq 8(%rdi),%rax
	movq %rax,%rsi
	movq 0(%rdi),%rax
	movq %rax,%rdx
	xorq %rcx,%rcx
	cmpq %rsi,%rcx
	jge L.4
L.2:
	movq %rdx,%rbx
	movq %rcx,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq $0,0(%rbx)
	movq %rcx,%rax
	addq $1,%rax
	movq %rax,%rcx
L.3:
	cmpq %rsi,%rcx
	jl L.2
L.4:
	movq %rdi,%rax
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
