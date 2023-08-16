	.text
LocalArrayFixed:
	pushq %rbp
	movq %rsp,%rbp
	subq $96,%rsp
	movq %rbx, -88(%rbp)
L.4:
	movabsq $10,%rax
	movq %rax,%rcx
	movabsq $0,%rax
	movq %rax,%rbx
	cmpq %rcx,%rbx
	jge L.1
L.0:
	shlq $3,%rbx
	addq %rbx, %rbp
	movabsq $0,%rax
	movq %rax,-80(%rbp)
	addq $1,%rbx
#	movq %rbx,%rbx
	cmpq %rcx,%rbx
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
