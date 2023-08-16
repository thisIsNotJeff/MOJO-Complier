	.text
LargeLocal:
	pushq %rbp
	movq %rsp,%rbp
	subq $-2147483648,%rsp
	movq %rbx, -2147483648(%rbp)
L.4:
	movq $268435455,%rax
	xorq %rbx,%rbx
	cmpq %rax,%rbx
	jge L.1
L.0:
	movq %rbp,%rcx
	movq %rbx,%rdx
	shlq $3,%rdx
	addq %rdx,%rcx
	movq $0,-2147483640(%rcx)
#	movq %rbx,%rbx
	addq $1,%rbx
#	movq %rbx,%rbx
	cmpq %rax,%rbx
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
