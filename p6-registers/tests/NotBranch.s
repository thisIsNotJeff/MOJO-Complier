	.text
NotBranch.Not:
	pushq %rbp
	movq %rsp,%rbp
#	movq %rdi,%rdi
L.5:
	xorq %rax,%rax
	cmpq %rax,%rdi
	je L.1
L.2:
	xorq %rax,%rax
	jmp L.0
L.1:
	movq $1,%rax
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
	leaq L.3(%rip),%rdi
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
L.4:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.8:
	leaq L.4(%rip),%rdi
#	movq %rdi,%rdi
	call puts
	movabsq $1,%rdi
#	movq %rdi,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
