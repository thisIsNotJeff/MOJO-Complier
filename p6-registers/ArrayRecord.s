	.text
ArrayRecord:
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
	shlq $4,%rax
	addq %rax,%rbx
#	movq %rbx,%rbx
	movq %rbx,%rdi
	call malloc
	movq %rax,%rdx
	movq %rdx,%rax
	addq $16,%rax
	movq %rax,0(%rdx)
	movq %r12,8(%rdx)
	movq 8(%rdx),%rax
	movq %rax,%rsi
	movq 0(%rdx),%rax
	movq %rax,%rdi
	xorq %rax,%rax
	cmpq %rsi,%rax
	jge L.4
L.2:
	movq %rdi,%rbx
	movq %rax,%rcx
	shlq $4,%rcx
	addq %rcx,%rbx
#	movq %rbx,%rbx
	movq $0,0(%rbx)
	movq $0,8(%rbx)
#	movq %rax,%rax
	addq $1,%rax
#	movq %rax,%rax
L.3:
	cmpq %rsi,%rax
	jl L.2
L.4:
	movq %rdx,%rax
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
	call ArrayRecord
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
