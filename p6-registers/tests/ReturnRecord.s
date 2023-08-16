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
	movq -8(%rbp),%rbx
#	movq %rbx,%rbx
	movq %rbx,%rdi
	movq %rbp,%rsi
	addq $-32,%rsi
#	movq %rsi,%rsi
	movabsq $24,%rdx
#	movq %rdx,%rdx
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
	subq $48,%rsp
L.6:
	movq %rbp,%rdi
	addq $-48,%rdi
#	movq %rdi,%rdi
	call ReturnRecord.foo
#	movq %rax,%rax
	movq %rbp,%rdi
	addq $-24,%rdi
#	movq %rdi,%rdi
	movq %rax,%rsi
	movabsq $24,%rdx
#	movq %rdx,%rdx
	call memcpy
	movq -24(%rbp),%rdi
#	movq %rdi,%rdi
	addq $48,%rdi
#	movq %rdi,%rdi
	call putchar
	movabsq $10,%rdi
#	movq %rdi,%rdi
	call putchar
	movq -16(%rbp),%rdi
#	movq %rdi,%rdi
	addq $48,%rdi
#	movq %rdi,%rdi
	call putchar
	movabsq $10,%rdi
#	movq %rdi,%rdi
	call putchar
	movq -8(%rbp),%rdi
#	movq %rdi,%rdi
	addq $48,%rdi
#	movq %rdi,%rdi
	call putchar
	movabsq $10,%rdi
#	movq %rdi,%rdi
	call putchar
#	returnSink
	addq $48,%rsp
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
L.9:
	leaq L.4(%rip),%rdi
#	movq %rdi,%rdi
	call puts
	movabsq $1,%rdi
#	movq %rdi,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
