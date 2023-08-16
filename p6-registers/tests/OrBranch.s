	.text
OrBranch.Or:
	pushq %rbp
	movq %rsp,%rbp
#	movq %rdi,%rdi
#	movq %rsi,%rsi
L.6:
	xorq %rax,%rax
	cmpq %rax,%rdi
	je L.3
L.1:
	xorq %rax,%rax
	jmp L.0
L.3:
	xorq %rax,%rax
	cmpq %rax,%rsi
	jne L.1
L.2:
	movq $1,%rax
L.0:
#	returnSink
	popq %rbp
	ret
	.text
OrBranch:
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
	call OrBranch
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
	leaq L.4(%rip),%rdi
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
L.5:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.9:
	leaq L.5(%rip),%rdi
#	movq %rdi,%rdi
	call puts
	movabsq $1,%rdi
#	movq %rdi,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
