	.text
ReturnArray.foo:
	pushq %rbp
	movq %rsp,%rbp
	subq $48,%rsp
	movq %rbx, -40(%rbp)
	movq %rdi,-8(%rbp)
L.10:
	movabsq $3,%rax
	movq %rax,%rcx
	movabsq $0,%rax
	movq %rax,%rbx
	cmpq %rcx,%rbx
	jge L.2
L.1:
	shlq $3,%rbx
	addq %rbx, %rbp
	movabsq $0,%rax
	movq %rax,-32(%rbp)
	addq $1,%rbx
#	movq %rbx,%rbx
	cmpq %rcx,%rbx
	jl L.1
L.2:
	movabsq $0,%rax
	movq %rax,%rdx
	movabsq $2,%rax
	movq %rax,%rcx
	movabsq $1,%rax
	movq %rax,%rbx
L.3:
	movq %rdx,%rax
	shlq $3,%rax
	addq $-32,%rax
	addq %rax, %rbp
	movq %rax,0(%rbp)
	addq %rbx, %rdx
#	movq %rdx,%rdx
L.4:
	cmpq %rcx,%rdx
	jle L.3
L.5:
	movq -8(%rbp),%rax
	movq %rax,%rbx
	movq %rbx,%rdi
	addq $-32,%rbp
	movq %rbp,%rsi
	movabsq $24,%rax
	movq %rax,%rdx
	call memmove
	movq %rbx,%rax
L.0:
#	returnSink
	movq -40(%rbp),%rbx
	addq $48,%rsp
	popq %rbp
	ret
L.6:
	call badSub
	.text
ReturnArray:
	pushq %rbp
	movq %rsp,%rbp
	subq $48,%rsp
L.11:
	addq $-48,%rbp
	movq %rbp,%rdi
	call ReturnArray.foo
#	movq %rax,%rax
	addq $-24,%rbp
	movq %rbp,%rdi
	movq %rax,%rsi
	movabsq $24,%rax
	movq %rax,%rdx
	call memmove
	movq -24(%rbp),%rax
	addq $48,%rax
	movq %rax,%rdi
	call putchar
	movabsq $10,%rax
	movq %rax,%rdi
	call putchar
	movq -16(%rbp),%rax
	addq $48,%rax
	movq %rax,%rdi
	call putchar
	movabsq $10,%rax
	movq %rax,%rdi
	call putchar
	movq -8(%rbp),%rax
	addq $48,%rax
	movq %rax,%rdi
	call putchar
	movabsq $10,%rax
	movq %rax,%rdi
	call putchar
#	returnSink
	addq $48,%rsp
	popq %rbp
	ret
L.7:
	call badSub
	.globl main
	.text
main:
	pushq %rbp
	movq %rsp,%rbp
L.12:
	call ReturnArray
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.8:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
	pushq %rbp
	movq %rsp,%rbp
L.13:
	leaq L.8(%rip),%rax
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
L.9:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.14:
	leaq L.9(%rip),%rax
	movq %rax,%rdi
	call puts
	movabsq $1,%rax
	movq %rax,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
