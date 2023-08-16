	.data
	.balign 8
QueensStaticOpen.row:
	.quad 0
	.data
	.balign 8
QueensStaticOpen.col:
	.quad 0
	.data
	.balign 8
QueensStaticOpen.diag1:
	.quad 0
	.data
	.balign 8
QueensStaticOpen.diag2:
	.quad 0
	.text
QueensStaticOpen.Solve:
	pushq %rbp
	movq %rsp,%rbp
	subq $48,%rsp
	movq %r15, -40(%rbp)
	movq %r14, -32(%rbp)
	movq %r13, -24(%rbp)
	movq %r12, -16(%rbp)
	movq %rbx, -8(%rbp)
	movq %rdi,%rbx
L.81:
	leaq QueensStaticOpen.col(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.4
L.5:
	movq 8(%rax),%rax
	cmpq %rax,%rbx
	je L.1
L.2:
	xorq %r13,%r13
	leaq QueensStaticOpen.row(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.4
L.6:
	movq 8(%rax),%r12
#	movq %r12,%r12
	subq $1,%r12
#	movq %r12,%r12
	movq $1,%r14
L.8:
	cmpq %r12,%r13
	jle L.7
L.9:
L.3:
	jmp L.0
L.1:
	call QueensStaticOpen.Print
	jmp L.3
L.7:
	movq %r13,%r15
	leaq QueensStaticOpen.row(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.4
L.14:
#	movq %rax,%rax
	movq %r15,%rcx
	xorq %rdx,%rdx
	cmpq %rdx,%rcx
	jl L.15
L.16:
	movq 8(%rax),%rdx
	cmpq %rdx,%rcx
	jge L.15
L.17:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.13
L.11:
#	movq %r13,%r13
	addq %r14,%r13
#	movq %r13,%r13
	jmp L.8
L.13:
	leaq QueensStaticOpen.diag1(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.4
L.18:
#	movq %rax,%rax
	movq %r15,%rcx
	addq %rbx,%rcx
#	movq %rcx,%rcx
	xorq %rdx,%rdx
	cmpq %rdx,%rcx
	jl L.15
L.19:
	movq 8(%rax),%rdx
	cmpq %rdx,%rcx
	jge L.15
L.20:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jne L.11
L.12:
	leaq QueensStaticOpen.diag2(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.4
L.21:
#	movq %rax,%rax
	movq %r15,%rcx
	movabsq $8,%rdx
#	movq %rdx,%rdx
	subq $1,%rdx
	addq %rdx,%rcx
#	movq %rcx,%rcx
	subq %rbx,%rcx
#	movq %rcx,%rcx
	xorq %rdx,%rdx
	cmpq %rdx,%rcx
	jl L.15
L.22:
	movq 8(%rax),%rdx
	cmpq %rdx,%rcx
	jge L.15
L.23:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jne L.11
L.10:
	leaq QueensStaticOpen.row(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.4
L.24:
#	movq %rax,%rax
	movq %r15,%rcx
	xorq %rdx,%rdx
	cmpq %rdx,%rcx
	jl L.15
L.25:
	movq 8(%rax),%rdx
	cmpq %rdx,%rcx
	jge L.15
L.26:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $1,0(%rax)
	leaq QueensStaticOpen.diag1(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.4
L.27:
#	movq %rax,%rax
	movq %r15,%rcx
	addq %rbx,%rcx
#	movq %rcx,%rcx
	xorq %rdx,%rdx
	cmpq %rdx,%rcx
	jl L.15
L.28:
	movq 8(%rax),%rdx
	cmpq %rdx,%rcx
	jge L.15
L.29:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $1,0(%rax)
	leaq QueensStaticOpen.diag2(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.4
L.30:
#	movq %rax,%rax
	movq %r15,%rcx
	movabsq $8,%rdx
#	movq %rdx,%rdx
	subq $1,%rdx
	addq %rdx,%rcx
#	movq %rcx,%rcx
	subq %rbx,%rcx
#	movq %rcx,%rcx
	xorq %rdx,%rdx
	cmpq %rdx,%rcx
	jl L.15
L.31:
	movq 8(%rax),%rdx
	cmpq %rdx,%rcx
	jge L.15
L.32:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $1,0(%rax)
	leaq QueensStaticOpen.col(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.4
L.33:
#	movq %rax,%rax
	movq %rbx,%rcx
	xorq %rdx,%rdx
	cmpq %rdx,%rcx
	jl L.15
L.34:
	movq 8(%rax),%rdx
	cmpq %rdx,%rcx
	jge L.15
L.35:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq %r15,0(%rax)
	movq %rbx,%rdi
	addq $1,%rdi
#	movq %rdi,%rdi
	call QueensStaticOpen.Solve
	leaq QueensStaticOpen.row(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.4
L.36:
#	movq %rax,%rax
	movq %r15,%rcx
	xorq %rdx,%rdx
	cmpq %rdx,%rcx
	jl L.15
L.37:
	movq 8(%rax),%rdx
	cmpq %rdx,%rcx
	jge L.15
L.38:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $0,0(%rax)
	leaq QueensStaticOpen.diag1(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.4
L.39:
#	movq %rax,%rax
	movq %r15,%rcx
	addq %rbx,%rcx
#	movq %rcx,%rcx
	xorq %rdx,%rdx
	cmpq %rdx,%rcx
	jl L.15
L.40:
	movq 8(%rax),%rdx
	cmpq %rdx,%rcx
	jge L.15
L.41:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $0,0(%rax)
	leaq QueensStaticOpen.diag2(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.4
L.42:
#	movq %rax,%rax
#	movq %r15,%r15
	movabsq $8,%rcx
#	movq %rcx,%rcx
	subq $1,%rcx
	addq %rcx,%r15
#	movq %r15,%r15
	subq %rbx,%r15
#	movq %r15,%r15
	xorq %rcx,%rcx
	cmpq %rcx,%r15
	jl L.15
L.43:
	movq 8(%rax),%rcx
	cmpq %rcx,%r15
	jge L.15
L.44:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %r15,%r15
	shlq $3,%r15
	addq %r15,%rax
	movq $0,0(%rax)
	jmp L.11
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
L.4:
	call badPtr
L.15:
	call badSub
	.text
QueensStaticOpen.Print:
	pushq %rbp
	movq %rsp,%rbp
	subq $48,%rsp
	movq %r15, -48(%rbp)
	movq %r14, -40(%rbp)
	movq %r13, -32(%rbp)
	movq %r12, -24(%rbp)
	movq %rbx, -16(%rbp)
L.82:
	xorq %rbx,%rbx
	leaq QueensStaticOpen.col(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.46
L.47:
	movq 8(%rax),%rax
#	movq %rax,%rax
	subq $1,%rax
#	movq %rax,%rax
	movq %rax,-8(%rbp)
	movq $1,%r15
L.49:
	movq -8(%rbp),%rax
#	movq %rax,%rax
	cmpq %rax,%rbx
	jle L.48
L.50:
	movabsq $10,%rdi
#	movq %rdi,%rdi
	call putchar
	jmp L.45
L.48:
#	movq %rbx,%rbx
	xorq %r12,%r12
	leaq QueensStaticOpen.row(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.46
L.51:
	movq 8(%rax),%rax
#	movq %rax,%rax
	subq $1,%rax
	movq %rax,%r13
	movq $1,%r14
L.53:
	cmpq %r13,%r12
	jle L.52
L.54:
	movabsq $10,%rdi
#	movq %rdi,%rdi
	call putchar
	movq %rbx,%rax
	addq %r15,%rax
	movq %rax,%rbx
	jmp L.49
L.52:
#	movq %r12,%r12
	movabsq $32,%rdi
#	movq %rdi,%rdi
	call putchar
	leaq QueensStaticOpen.col(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.46
L.59:
#	movq %rax,%rax
	movq %rbx,%rcx
	xorq %rdx,%rdx
	cmpq %rdx,%rcx
	jl L.55
L.60:
	movq 8(%rax),%rdx
	cmpq %rdx,%rcx
	jge L.55
L.61:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rax
	cmpq %r12,%rax
	je L.56
L.57:
	movabsq $46,%rdi
#	movq %rdi,%rdi
	call putchar
L.58:
	movq %r12,%rax
	addq %r14,%rax
	movq %rax,%r12
	jmp L.53
L.56:
	movabsq $81,%rdi
#	movq %rdi,%rdi
	call putchar
	jmp L.58
L.45:
#	returnSink
	movq -16(%rbp),%rbx
	movq -24(%rbp),%r12
	movq -32(%rbp),%r13
	movq -40(%rbp),%r14
	movq -48(%rbp),%r15
	addq $48,%rsp
	popq %rbp
	ret
L.46:
	call badPtr
L.55:
	call badSub
	.text
QueensStaticOpen:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %r12, -16(%rbp)
	movq %rbx, -8(%rbp)
L.83:
	leaq QueensStaticOpen.row(%rip),%rax
	movq $0,0(%rax)
	leaq QueensStaticOpen.col(%rip),%rax
	movq $0,0(%rax)
	leaq QueensStaticOpen.diag1(%rip),%rax
	movq $0,0(%rax)
	leaq QueensStaticOpen.diag2(%rip),%rax
	movq $0,0(%rax)
	movq $8,%r12
	xorq %rax,%rax
	cmpq %rax,%r12
	jl L.62
L.63:
#	movq %r12,%r12
	movq $16,%rdi
#	movq %rdi,%rdi
	movq %r12,%rax
	shlq $3,%rax
	addq %rax,%rdi
#	movq %rdi,%rdi
#	movq %rdi,%rdi
	call malloc
#	movq %rax,%rax
	movq %rax,%rbx
	addq $16,%rbx
	movq %rbx,0(%rax)
	movq %r12,8(%rax)
	movq 8(%rax),%rcx
#	movq %rcx,%rcx
	movq 0(%rax),%rbx
#	movq %rbx,%rbx
	xorq %rdx,%rdx
	cmpq %rcx,%rdx
	jge L.66
L.64:
	movq %rbx,%rdi
	movq %rdx,%rsi
	shlq $3,%rsi
	addq %rsi,%rdi
	movq $0,0(%rdi)
#	movq %rdx,%rdx
	addq $1,%rdx
#	movq %rdx,%rdx
L.65:
	cmpq %rcx,%rdx
	jl L.64
L.66:
	leaq QueensStaticOpen.row(%rip),%rbx
	movq %rax,0(%rbx)
	movq $8,%r12
	xorq %rax,%rax
	cmpq %rax,%r12
	jl L.62
L.67:
#	movq %r12,%r12
	movq $16,%rdi
#	movq %rdi,%rdi
	movq %r12,%rax
	shlq $3,%rax
	addq %rax,%rdi
#	movq %rdi,%rdi
#	movq %rdi,%rdi
	call malloc
#	movq %rax,%rax
	movq %rax,%rbx
	addq $16,%rbx
	movq %rbx,0(%rax)
	movq %r12,8(%rax)
	movq 8(%rax),%rcx
#	movq %rcx,%rcx
	movq 0(%rax),%rbx
#	movq %rbx,%rbx
	xorq %rdx,%rdx
	cmpq %rcx,%rdx
	jge L.70
L.68:
	movq %rbx,%rdi
	movq %rdx,%rsi
	shlq $3,%rsi
	addq %rsi,%rdi
	movq $0,0(%rdi)
#	movq %rdx,%rdx
	addq $1,%rdx
#	movq %rdx,%rdx
L.69:
	cmpq %rcx,%rdx
	jl L.68
L.70:
	leaq QueensStaticOpen.col(%rip),%rbx
	movq %rax,0(%rbx)
	movabsq $8,%rbx
#	movq %rbx,%rbx
	addq $8,%rbx
#	movq %rbx,%rbx
	subq $1,%rbx
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	jl L.62
L.71:
#	movq %rbx,%rbx
	movq $16,%rdi
#	movq %rdi,%rdi
	movq %rbx,%rax
	shlq $3,%rax
	addq %rax,%rdi
#	movq %rdi,%rdi
#	movq %rdi,%rdi
	call malloc
#	movq %rax,%rax
	movq %rax,%rcx
	addq $16,%rcx
	movq %rcx,0(%rax)
	movq %rbx,8(%rax)
	movq 8(%rax),%rcx
#	movq %rcx,%rcx
	movq 0(%rax),%rbx
#	movq %rbx,%rbx
	xorq %rdx,%rdx
	cmpq %rcx,%rdx
	jge L.74
L.72:
	movq %rbx,%rdi
	movq %rdx,%rsi
	shlq $3,%rsi
	addq %rsi,%rdi
	movq $0,0(%rdi)
#	movq %rdx,%rdx
	addq $1,%rdx
#	movq %rdx,%rdx
L.73:
	cmpq %rcx,%rdx
	jl L.72
L.74:
	leaq QueensStaticOpen.diag1(%rip),%rbx
	movq %rax,0(%rbx)
	movabsq $8,%rbx
#	movq %rbx,%rbx
	addq $8,%rbx
#	movq %rbx,%rbx
	subq $1,%rbx
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	jl L.62
L.75:
#	movq %rbx,%rbx
	movq $16,%rdi
#	movq %rdi,%rdi
	movq %rbx,%rax
	shlq $3,%rax
	addq %rax,%rdi
#	movq %rdi,%rdi
#	movq %rdi,%rdi
	call malloc
#	movq %rax,%rax
	movq %rax,%rcx
	addq $16,%rcx
	movq %rcx,0(%rax)
	movq %rbx,8(%rax)
	movq 8(%rax),%rcx
#	movq %rcx,%rcx
	movq 0(%rax),%rbx
#	movq %rbx,%rbx
	xorq %rdx,%rdx
	cmpq %rcx,%rdx
	jge L.78
L.76:
	movq %rbx,%rdi
	movq %rdx,%rsi
	shlq $3,%rsi
	addq %rsi,%rdi
	movq $0,0(%rdi)
#	movq %rdx,%rdx
	addq $1,%rdx
#	movq %rdx,%rdx
L.77:
	cmpq %rcx,%rdx
	jl L.76
L.78:
	leaq QueensStaticOpen.diag2(%rip),%rbx
	movq %rax,0(%rbx)
	xorq %rdi,%rdi
#	movq %rdi,%rdi
	call QueensStaticOpen.Solve
#	returnSink
	movq -8(%rbp),%rbx
	movq -16(%rbp),%r12
	addq $16,%rsp
	popq %rbp
	ret
L.62:
	call badSub
	.globl main
	.text
main:
	pushq %rbp
	movq %rsp,%rbp
L.84:
	call QueensStaticOpen
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.79:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
	pushq %rbp
	movq %rsp,%rbp
L.85:
	leaq L.79(%rip),%rdi
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
L.80:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.86:
	leaq L.80(%rip),%rdi
#	movq %rdi,%rdi
	call puts
	movabsq $1,%rdi
#	movq %rdi,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
