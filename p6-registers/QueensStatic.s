	.data
	.balign 8
QueensStatic.row:
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.data
	.balign 8
QueensStatic.col:
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.data
	.balign 8
QueensStatic.diag1:
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.data
	.balign 8
QueensStatic.diag2:
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.quad 0
	.text
QueensStatic.Solve:
	pushq %rbp
	movq %rsp,%rbp
	subq $48,%rsp
	movq %r15, -40(%rbp)
	movq %r14, -32(%rbp)
	movq %r13, -24(%rbp)
	movq %r12, -16(%rbp)
	movq %rbx, -8(%rbp)
	movq %rdi,%r15
L.26:
	movabsq $8,%rax
	cmpq %rax,%r15
	je L.1
L.2:
	xorq %r14,%r14
	movq $7,%r12
	movq $1,%r13
L.4:
#	movq %r14,%r14
	leaq QueensStatic.row(%rip),%rax
#	movq %rax,%rax
	movq %r14,%rbx
	shlq $3,%rbx
	addq %rbx,%rax
	movq 0(%rax),%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.10
L.8:
	movq %r14,%rax
	addq %r13,%rax
	movq %rax,%r14
L.5:
	cmpq %r12,%r14
	jle L.4
L.6:
L.3:
	jmp L.0
L.1:
	call QueensStatic.Print
	jmp L.3
L.10:
	leaq QueensStatic.diag1(%rip),%rax
	movq %rax,%rbx
	movq %r14,%rax
	addq %r15,%rax
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq 0(%rbx),%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	jne L.8
L.9:
	leaq QueensStatic.diag2(%rip),%rax
	movq %rax,%rbx
	movq %r14,%rax
	movabsq $8,%rcx
#	movq %rcx,%rcx
	subq $1,%rcx
	addq %rcx,%rax
#	movq %rax,%rax
	subq %r15,%rax
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq 0(%rbx),%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	jne L.8
L.7:
	leaq QueensStatic.row(%rip),%rax
#	movq %rax,%rax
	movq %r14,%rbx
	shlq $3,%rbx
	addq %rbx,%rax
	movq $1,0(%rax)
	leaq QueensStatic.diag1(%rip),%rax
	movq %rax,%rbx
	movq %r14,%rax
	addq %r15,%rax
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq $1,0(%rbx)
	leaq QueensStatic.diag2(%rip),%rax
	movq %rax,%rbx
	movq %r14,%rax
	movabsq $8,%rcx
#	movq %rcx,%rcx
	subq $1,%rcx
	addq %rcx,%rax
#	movq %rax,%rax
	subq %r15,%rax
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq $1,0(%rbx)
	leaq QueensStatic.col(%rip),%rax
#	movq %rax,%rax
	movq %r15,%rbx
	shlq $3,%rbx
	addq %rbx,%rax
	movq %r14,0(%rax)
	movq %r15,%rax
	addq $1,%rax
	movq %rax,%rdi
	call QueensStatic.Solve
	leaq QueensStatic.row(%rip),%rax
#	movq %rax,%rax
	movq %r14,%rbx
	shlq $3,%rbx
	addq %rbx,%rax
	movq $0,0(%rax)
	leaq QueensStatic.diag1(%rip),%rax
	movq %rax,%rbx
	movq %r14,%rax
	addq %r15,%rax
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq $0,0(%rbx)
	leaq QueensStatic.diag2(%rip),%rax
	movq %rax,%rbx
	movq %r14,%rcx
	movabsq $8,%rax
#	movq %rax,%rax
	subq $1,%rax
	addq %rax,%rcx
	movq %rcx,%rax
	subq %r15,%rax
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq $0,0(%rbx)
	jmp L.8
L.0:
#	returnSink
	movq -8(%rbp),%rbx
	movq -16(%rbp),%r12
	movq -24(%rbp),%r13
	movq -32(%rbp),%r14
	movq -40(%rbp),%r15
	addq $48,%rsp
	popq %rbp
	ret
L.11:
	call badSub
	.text
QueensStatic.Print:
	pushq %rbp
	movq %rsp,%rbp
	subq $64,%rsp
	movq %r15, -56(%rbp)
	movq %r14, -48(%rbp)
	movq %r13, -40(%rbp)
	movq %r12, -32(%rbp)
	movq %rbx, -24(%rbp)
L.27:
	xorq %rax,%rax
	movq %rax,-16(%rbp)
	movq $7,%r12
	movq $1,%r13
L.13:
	movq -16(%rbp),%rax
#	movq %rax,%rax
#	movq %rax,%rax
	movq %rax,-8(%rbp)
	xorq %rbx,%rbx
	movq $7,%r14
	movq $1,%r15
L.16:
#	movq %rbx,%rbx
	movabsq $32,%rax
	movq %rax,%rdi
	call putchar
	leaq QueensStatic.col(%rip),%rax
#	movq %rax,%rax
	movq -8(%rbp),%rcx
#	movq %rcx,%rcx
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rax
	cmpq %rbx,%rax
	je L.20
L.21:
	movabsq $46,%rax
	movq %rax,%rdi
	call putchar
L.22:
	movq %rbx,%rax
	addq %r15,%rax
	movq %rax,%rbx
L.17:
	cmpq %r14,%rbx
	jle L.16
L.18:
	movabsq $10,%rax
	movq %rax,%rdi
	call putchar
	movq -16(%rbp),%rax
#	movq %rax,%rax
#	movq %rax,%rax
	addq %r13,%rax
#	movq %rax,%rax
	movq %rax,-16(%rbp)
L.14:
	movq -16(%rbp),%rax
#	movq %rax,%rax
	cmpq %r12,%rax
	jle L.13
L.15:
	movabsq $10,%rax
	movq %rax,%rdi
	call putchar
	jmp L.12
L.20:
	movabsq $81,%rax
	movq %rax,%rdi
	call putchar
	jmp L.22
L.12:
#	returnSink
	movq -24(%rbp),%rbx
	movq -32(%rbp),%r12
	movq -40(%rbp),%r13
	movq -48(%rbp),%r14
	movq -56(%rbp),%r15
	addq $64,%rsp
	popq %rbp
	ret
L.19:
	call badSub
	.text
QueensStatic:
	pushq %rbp
	movq %rsp,%rbp
L.28:
	xorq %rax,%rax
	movq %rax,%rdi
	call QueensStatic.Solve
#	returnSink
	popq %rbp
	ret
L.23:
	call badSub
	.globl main
	.text
main:
	pushq %rbp
	movq %rsp,%rbp
L.29:
	call QueensStatic
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.24:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
	pushq %rbp
	movq %rsp,%rbp
L.30:
	leaq L.24(%rip),%rax
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
L.25:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.31:
	leaq L.25(%rip),%rax
	movq %rax,%rdi
	call puts
	movabsq $1,%rax
	movq %rax,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
