	.text
ReturnArray.foo:
	pushq %rbp
	movq %rsp,%rbp
	subq $48,%rsp
	movq %rbx, -40(%rbp)
	movq %rdi,-8(%rbp)
L.10:
	movq $3,%rax
	xorq %rbx,%rbx
	cmpq %rax,%rbx
	jge L.2
L.1:
	movq %rbp,%rcx
	movq %rbx,%rdx
	shlq $3,%rdx
	addq %rdx,%rcx
	movq $0,-32(%rcx)
#	movq %rbx,%rbx
	addq $1,%rbx
#	movq %rbx,%rbx
	cmpq %rax,%rbx
	jl L.1
L.2:
	xorq %rax,%rax
	movq $2,%rdx
	movq $1,%rsi
L.3:
#	movq %rax,%rax
	movq %rbp,%rbx
	movq %rax,%rcx
	shlq $3,%rcx
#	movq %rcx,%rcx
	addq $-32,%rcx
	addq %rcx,%rbx
	movq %rax,0(%rbx)
#	movq %rax,%rax
	addq %rsi,%rax
#	movq %rax,%rax
L.4:
	cmpq %rdx,%rax
	jle L.3
L.5:
	movq -8(%rbp),%rax
	movq %rax,%rbx
	movq %rbx,%rdi
	movq %rbp,%rax
	addq $-32,%rax
	movq %rax,%rsi
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
	subq $64,%rsp
	movq %rbx, -56(%rbp)
L.11:
	movq %rbp,%rax
	addq $-48,%rax
	movq %rax,%rdi
	call ReturnArray.foo
#	movq %rax,%rax
	movq %rbp,%rbx
	addq $-24,%rbx
	movq %rbx,%rdi
	movq %rax,%rsi
	movabsq $24,%rax
	movq %rax,%rdx
	call memmove
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
