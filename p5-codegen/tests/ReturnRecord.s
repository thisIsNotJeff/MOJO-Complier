	.text
ReturnRecord.foo:
	pushq %rbp
	movq %rsp,%rbp
	subq $48,%rsp
	movq %rbx, -40(%rbp)
	movq %rdi,-8(%rbp)
L.5:
	movq $0,-32(%rbp)
	movq $0,-24(%rbp)
	movq $0,-16(%rbp)
	movq $1,-32(%rbp)
	movq $2,-24(%rbp)
	movq $3,-16(%rbp)
	movq -8(%rbp),%rax
	movq %rax,%rbx
	movq %rbx,%rdi
	movq %rbp,%rax
	addq $-32,%rax
	movq %rax,%rsi
	movabsq $24,%rax
	movq %rax,%rdx
	call memcpy
	movq %rbx,%rax
L.0:
#	returnSink
	movq -40(%rbp),%rbx
	addq $48,%rsp
	popq %rbp
	ret
L.1:
	call badSub
	.text
ReturnRecord:
	pushq %rbp
	movq %rsp,%rbp
	subq $64,%rsp
	movq %rbx, -56(%rbp)
L.6:
	movq %rbp,%rax
	addq $-48,%rax
	movq %rax,%rdi
	call ReturnRecord.foo
	movq %rax,%rbx
	movq %rbp,%rax
	addq $-24,%rax
	movq %rax,%rdi
	movq %rbx,%rsi
	movabsq $24,%rax
	movq %rax,%rdx
	call memcpy
	movq -24(%rbp),%rax
#	movq %rax,%rax
	addq $48,%rax
	movq %rax,%rdi
	call putchar
	movabsq $10,%rax
	movq %rax,%rdi
	call putchar
	movq -16(%rbp),%rax
#	movq %rax,%rax
	addq $48,%rax
	movq %rax,%rdi
	call putchar
	movabsq $10,%rax
	movq %rax,%rdi
	call putchar
	movq -8(%rbp),%rax
#	movq %rax,%rax
	addq $48,%rax
	movq %rax,%rdi
	call putchar
	movabsq $10,%rax
	movq %rax,%rdi
	call putchar
#	returnSink
	movq -56(%rbp),%rbx
	addq $64,%rsp
	popq %rbp
	ret
L.2:
	call badSub
	.globl main
	.text
main:
	pushq %rbp
	movq %rsp,%rbp
L.7:
	call ReturnRecord
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
