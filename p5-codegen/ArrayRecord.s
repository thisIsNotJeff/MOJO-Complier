	.text
ArrayRecord:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %r12, -16(%rbp)
	movq %rbx, -8(%rbp)
L.7:
	movabsq $10,%rax
	movq %rax,%r12
	cmpq $0,%r12
	jl L.0
L.1:
	movq %r12,%rbx
	movabsq $16,%rax
#	movq %rax,%rax
	shlq $4,%rbx
	addq %rbx, %rax
#	movq %rax,%rax
	movq %rax,%rdi
	call malloc
	movq %rax,%rsi
	addq $16,%rsi
	movq %rsi,0(%rsi)
	movq %r12,8(%rsi)
	movq 8(%rsi),%rax
	movq %rax,%rdx
	movq 0(%rsi),%rax
	movq %rax,%rbx
	movabsq $0,%rax
	movq %rax,%rcx
	cmpq %rdx,%rcx
	jge L.4
L.2:
	shlq $4,%rcx
	addq %rcx, %rbx
#	movq %rbx,%rbx
	movabsq $0,%rax
	movq %rax,0(%rbx)
	movabsq $0,%rax
	movq %rax,8(%rbx)
	addq $1,%rcx
#	movq %rcx,%rcx
L.3:
	cmpq %rdx,%rcx
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
