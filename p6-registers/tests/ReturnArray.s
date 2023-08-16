	.text
ReturnArray.foo:
	pushq %rbp
	movq %rsp,%rbp
	subq $48,%rsp
	movq %rbx, -40(%rbp)
	movq %rdi,-8(%rbp)
L.10:
	movq $3,%rdx
	xorq %rax,%rax
	cmpq %rdx,%rax
	jge L.2
L.1:
	movq %rbp,%rcx
	movq %rax,%rbx
	shlq $3,%rbx
	addq %rbx,%rcx
	movq $0,-32(%rcx)
#	movq %rax,%rax
	addq $1,%rax
#	movq %rax,%rax
	cmpq %rdx,%rax
	jl L.1
L.2:
	xorq %rax,%rax
	movq $2,%rsi
	movq $1,%rdx
L.3:
#	movq %rax,%rax
	movq %rbp,%rcx
	movq %rax,%rbx
	shlq $3,%rbx
#	movq %rbx,%rbx
	addq $-32,%rbx
	addq %rbx,%rcx
	movq %rax,0(%rcx)
#	movq %rax,%rax
	addq %rdx,%rax
#	movq %rax,%rax
L.4:
	cmpq %rsi,%rax
	jle L.3
L.5:
	movq -8(%rbp),%rbx
#	movq %rbx,%rbx
	movq %rbx,%rdi
	movq %rbp,%rsi
	addq $-32,%rsi
#	movq %rsi,%rsi
	movabsq $24,%rdx
#	movq %rdx,%rdx
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
	movq %rbp,%rdi
	addq $-48,%rdi
#	movq %rdi,%rdi
	call ReturnArray.foo
#	movq %rax,%rax
	movq %rbp,%rdi
	addq $-24,%rdi
#	movq %rdi,%rdi
	movq %rax,%rsi
	movabsq $24,%rdx
#	movq %rdx,%rdx
	call memmove
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
	leaq L.8(%rip),%rdi
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
L.9:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.14:
	leaq L.9(%rip),%rdi
#	movq %rdi,%rdi
	call puts
	movabsq $1,%rdi
#	movq %rdi,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
