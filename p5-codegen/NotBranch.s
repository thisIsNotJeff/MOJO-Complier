	.text
NotBranch.Not:
	pushq %rbp
	movq %rsp,%rbp
	movq %rdi,%rax
L.5:
	cmpq $0,%rax
	je L.1
L.2:
	movabsq $0,%rax
#	movq %rax,%rax
	jmpq L.0
L.1:
	movabsq $1,%rax
#	movq %rax,%rax
L.0:
#	returnSink
	popq %rbp
	ret
	.text
NotBranch:
	pushq %rbp
	movq %rsp,%rbp
#	returnSink
	popq %rbp
	ret
	.globl main
	.text
main:
	pushq %rbp
	movq %rsp,%rbp
L.6:
	call NotBranch
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
	.data
	.balign 8
L.4:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.8:
	leaq L.4(%rip),%rax
	movq %rax,%rdi
	call puts
	movabsq $1,%rax
	movq %rax,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
