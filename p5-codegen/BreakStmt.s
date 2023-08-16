	.text
BreakStmt:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %rbx, -8(%rbp)
L.5:
	movabsq $0,%rax
	movq %rax,%rbx
	movabsq $0,%rax
#	movq %rax,%rax
L.0:
	cmpq $0,%rbx
	je L.2
L.1:
	jmpq L.2
L.6:
	cmpq $0,%rax
	je L.0
L.2:
#	returnSink
	movq -8(%rbp),%rbx
	addq $16,%rsp
	popq %rbp
	ret
	.globl main
	.text
main:
	pushq %rbp
	movq %rsp,%rbp
L.7:
	call BreakStmt
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.3:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
	pushq %rbp
	movq %rsp,%rbp
L.8:
	leaq L.3(%rip),%rax
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
L.4:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.9:
	leaq L.4(%rip),%rax
	movq %rax,%rdi
	call puts
	movabsq $1,%rax
	movq %rax,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
