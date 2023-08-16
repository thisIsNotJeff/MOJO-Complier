	.text
Compare.LT:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %rbx, -8(%rbp)
	movq %rdi,%rcx
	movq %rsi,%rbx
L.14:
	movabsq $0,%rax
#	movq %rax,%rax
	cmpq %rbx,%rcx
	jl L.1
L.2:
#	movq %rax,%rax
	jmpq L.0
L.1:
	movabsq $1,%rax
#	movq %rax,%rax
	jmpq L.2
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
	movq %rdi,%rcx
	movq %rsi,%rbx
L.15:
	movabsq $0,%rax
#	movq %rax,%rax
	cmpq %rbx,%rcx
	jle L.4
L.5:
#	movq %rax,%rax
	jmpq L.3
L.4:
	movabsq $1,%rax
#	movq %rax,%rax
	jmpq L.5
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
	movq %rdi,%rcx
	movq %rsi,%rbx
L.16:
	movabsq $0,%rax
#	movq %rax,%rax
	cmpq %rbx,%rcx
	jg L.7
L.8:
#	movq %rax,%rax
	jmpq L.6
L.7:
	movabsq $1,%rax
#	movq %rax,%rax
	jmpq L.8
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
	movq %rdi,%rcx
	movq %rsi,%rbx
L.17:
	movabsq $0,%rax
#	movq %rax,%rax
	cmpq %rbx,%rcx
	jge L.10
L.11:
#	movq %rax,%rax
	jmpq L.9
L.10:
	movabsq $1,%rax
#	movq %rax,%rax
	jmpq L.11
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
