	.data
	.balign 8
FieldAccess.r:
	.quad 0
	.quad 0
	.quad 0
	.text
FieldAccess:
	pushq %rbp
	movq %rsp,%rbp
L.3:
	leaq FieldAccess.r(%rip),%rax
	movq $1,0(%rax)
	leaq FieldAccess.r(%rip),%rax
	movq $2,8(%rax)
	leaq FieldAccess.r(%rip),%rax
	movq $3,16(%rax)
#	returnSink
	popq %rbp
	ret
L.0:
	call badSub
	.globl main
	.text
main:
	pushq %rbp
	movq %rsp,%rbp
L.4:
	call FieldAccess
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.1:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
	pushq %rbp
	movq %rsp,%rbp
L.5:
	leaq L.1(%rip),%rax
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
L.2:	.asciz	"Subscript out of bounds"
	.text
badSub:
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
