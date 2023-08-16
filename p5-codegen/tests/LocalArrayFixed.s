	.text
LocalArrayFixed:
	pushq %rbp
	movq %rsp,%rbp
	subq $96,%rsp
	movq %rbx, -88(%rbp)
L.4:
	movq $10,%rdx
	xorq %rcx,%rcx
	cmpq %rdx,%rcx
	jge L.1
L.0:
	movq %rbp,%rbx
	movq %rcx,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq $0,-80(%rbx)
	movq %rcx,%rax
	addq $1,%rax
	movq %rax,%rcx
	cmpq %rdx,%rcx
	jl L.0
L.1:
#	returnSink
	movq -88(%rbp),%rbx
	addq $96,%rsp
	popq %rbp
	ret
	.globl main
	.text
main:
	pushq %rbp
	movq %rsp,%rbp
L.5:
	call LocalArrayFixed
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
