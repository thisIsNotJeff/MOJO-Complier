	.data
	.balign 8
AssignRecord.a:
	.quad 0
	.quad 0
	.text
AssignRecord:
	pushq %rbp
	movq %rsp,%rbp
	subq $32,%rsp
	movq %rbx, -24(%rbp)
L.3:
	movq $0,-16(%rbp)
	movq $0,-8(%rbp)
	leaq AssignRecord.a(%rip),%rax
	movq $1,0(%rax)
	leaq AssignRecord.a(%rip),%rax
	movq $2,8(%rax)
	movq %rbp,%rax
	addq $-16,%rax
	movq %rax,%rdi
	leaq AssignRecord.a(%rip),%rax
	movq %rax,%rsi
	movabsq $16,%rax
	movq %rax,%rdx
	call memcpy
	movq -16(%rbp),%rax
#	movq %rax,%rax
	movq -8(%rbp),%rbx
	addq %rbx,%rax
	movq %rax,%rdi
	call exit
#	returnSink
	movq -24(%rbp),%rbx
	addq $32,%rsp
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
