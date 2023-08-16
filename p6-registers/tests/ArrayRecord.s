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
#	movq %r12,%r12
	movq $16,%rdi
#	movq %rdi,%rdi
	movq %r12,%rax
	shlq $4,%rax
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
	xorq %rdx,%rdx
	cmpq %rcx,%rdx
	jge L.4
L.2:
	movq %rbx,%rsi
	movq %rdx,%rdi
	shlq $4,%rdi
	addq %rdi,%rsi
#	movq %rsi,%rsi
	movq $0,0(%rsi)
	movq $0,8(%rsi)
#	movq %rdx,%rdx
	addq $1,%rdx
#	movq %rdx,%rdx
L.3:
	cmpq %rcx,%rdx
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
	leaq L.5(%rip),%rdi
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
L.6:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.10:
	leaq L.6(%rip),%rdi
#	movq %rdi,%rdi
	call puts
	movabsq $1,%rdi
#	movq %rdi,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
