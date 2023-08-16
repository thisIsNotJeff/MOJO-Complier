	.data
	.balign 8
AssignRecord.a:
	.quad 0
	.quad 0
	.text
AssignRecord:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
L.3:
	movq $0,-16(%rbp)
	movq $0,-8(%rbp)
	leaq AssignRecord.a(%rip),%rax
	movq $1,0(%rax)
	leaq AssignRecord.a(%rip),%rax
	movq $2,8(%rax)
	movq %rbp,%rdi
	addq $-16,%rdi
#	movq %rdi,%rdi
	leaq AssignRecord.a(%rip),%rsi
#	movq %rsi,%rsi
	movabsq $16,%rdx
#	movq %rdx,%rdx
	call memcpy
	movq -16(%rbp),%rdi
#	movq %rdi,%rdi
	movq -8(%rbp),%rax
	addq %rax,%rdi
#	movq %rdi,%rdi
	call exit
#	returnSink
	addq $16,%rsp
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
	call AssignRecord
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
	leaq L.1(%rip),%rdi
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
L.2:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.6:
	leaq L.2(%rip),%rdi
#	movq %rdi,%rdi
	call puts
	movabsq $1,%rdi
#	movq %rdi,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
