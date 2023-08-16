	.text
LargeLocal:
	pushq %rbp
	movq %rsp,%rbp
	subq $-2147483648,%rsp
	movq %rbx, -2147483648(%rbp)
L.4:
	movq $268435455,%rdx
	xorq %rcx,%rcx
	cmpq %rdx,%rcx
	jge L.1
L.0:
	movq %rbp,%rbx
	movq %rcx,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq $0,-2147483640(%rbx)
	movq %rcx,%rax
	addq $1,%rax
	movq %rax,%rcx
	cmpq %rdx,%rcx
	jl L.0
L.1:
#	returnSink
	movq -2147483648(%rbp),%rbx
	addq $-2147483648,%rsp
	popq %rbp
	ret
	.globl main
	.text
main:
	pushq %rbp
	movq %rsp,%rbp
L.5:
	call LargeLocal
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.2:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
	pushq %rbp
	movq %rsp,%rbp
L.6:
	leaq L.2(%rip),%rax
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
L.3:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.7:
	leaq L.3(%rip),%rax
	movq %rax,%rdi
	call puts
	movabsq $1,%rax
	movq %rax,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
