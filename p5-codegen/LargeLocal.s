	.text
LargeLocal:
	pushq %rbp
	movq %rsp,%rbp
	subq $-2147483648,%rsp
	movq %rbx, -2147483648(%rbp)
L.4:
	movabsq $268435455,%rax
	movq %rax,%rcx
	movabsq $0,%rax
	movq %rax,%rbx
	cmpq %rcx,%rbx
	jge L.1
L.0:
	shlq $3,%rbx
	addq %rbx, %rbp
	movabsq $0,%rax
	movq %rax,-2147483640(%rbp)
	addq $1,%rbx
#	movq %rbx,%rbx
	cmpq %rcx,%rbx
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
