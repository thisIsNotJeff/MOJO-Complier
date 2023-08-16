	.text
Compare.LT:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %rbx, -8(%rbp)
	movq %rdi,%rax
	movq %rsi,%rbx
L.14:
	xorq %rcx,%rcx
	cmpq %rbx,%rax
	jl L.1
L.2:
	movq %rcx,%rax
	jmp L.0
L.1:
	movq $1,%rcx
	jmp L.2
L.0:
#	returnSink
	movq -8(%rbp),%rbx
	addq $16,%rsp
	popq %rbp
	ret
	.text
Compare.LE:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %rbx, -8(%rbp)
	movq %rdi,%rax
	movq %rsi,%rbx
L.15:
	xorq %rcx,%rcx
	cmpq %rbx,%rax
	jle L.4
L.5:
	movq %rcx,%rax
	jmp L.3
L.4:
	movq $1,%rcx
	jmp L.5
L.3:
#	returnSink
	movq -8(%rbp),%rbx
	addq $16,%rsp
	popq %rbp
	ret
	.text
Compare.GT:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %rbx, -8(%rbp)
	movq %rdi,%rax
	movq %rsi,%rbx
L.16:
	xorq %rcx,%rcx
	cmpq %rbx,%rax
	jg L.7
L.8:
	movq %rcx,%rax
	jmp L.6
L.7:
	movq $1,%rcx
	jmp L.8
L.6:
#	returnSink
	movq -8(%rbp),%rbx
	addq $16,%rsp
	popq %rbp
	ret
	.text
Compare.GE:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %rbx, -8(%rbp)
	movq %rdi,%rax
	movq %rsi,%rbx
L.17:
	xorq %rcx,%rcx
	cmpq %rbx,%rax
	jge L.10
L.11:
	movq %rcx,%rax
	jmp L.9
L.10:
	movq $1,%rcx
	jmp L.11
L.9:
#	returnSink
	movq -8(%rbp),%rbx
	addq $16,%rsp
	popq %rbp
	ret
	.text
Compare:
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
L.18:
	call Compare
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.12:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
	pushq %rbp
	movq %rsp,%rbp
L.19:
	leaq L.12(%rip),%rax
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
L.13:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.20:
	leaq L.13(%rip),%rax
	movq %rax,%rdi
	call puts
	movabsq $1,%rax
	movq %rax,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
