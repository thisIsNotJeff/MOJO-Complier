	.text
AndBranch.And:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %rbx, -8(%rbp)
	movq %rdi,%rcx
	movq %rsi,%rbx
L.6:
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.2
L.3:
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.2
L.1:
	xorq %rax,%rax
	jmp L.0
L.2:
	movq $1,%rax
L.0:
#	returnSink
	movq -8(%rbp),%rbx
	addq $16,%rsp
	popq %rbp
	ret
	.text
AndBranch:
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
L.7:
	call AndBranch
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.4:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
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
	.data
	.balign 8
L.5:	.asciz	"Subscript out of bounds"
	.text
badSub:
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
