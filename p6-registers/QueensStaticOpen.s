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
	movq %rdi,%r15
L.81:
	leaq QueensStaticOpen.col(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.4
L.5:
	movq 8(%rax),%rax
	cmpq %rax,%r15
	je L.1
L.2:
	xorq %r14,%r14
	leaq QueensStaticOpen.row(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.4
L.6:
	movq 8(%rax),%rax
#	movq %rax,%rax
	subq $1,%rax
	movq %rax,%r12
	movq $1,%r13
L.8:
	cmpq %r12,%r14
	jle L.7
L.9:
L.3:
	jmp L.0
L.1:
	call QueensStaticOpen.Print
	jmp L.3
L.7:
#	movq %r14,%r14
	leaq QueensStaticOpen.row(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.4
L.14:
	movq %rax,%rbx
	movq %r14,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.15
L.16:
	movq 8(%rbx),%rax
	cmpq %rax,%rcx
	jge L.15
L.17:
	movq 0(%rbx),%rax
	movq %rax,%rbx
	movq %rcx,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq 0(%rbx),%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.13
L.11:
	movq %r14,%rax
	addq %r13,%rax
	movq %rax,%r14
	jmp L.8
L.13:
	leaq QueensStaticOpen.diag1(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.4
L.18:
	movq %rax,%rbx
	movq %r14,%rax
	addq %r15,%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.15
L.19:
	movq 8(%rbx),%rax
	cmpq %rax,%rcx
	jge L.15
L.20:
	movq 0(%rbx),%rax
	movq %rax,%rbx
	movq %rcx,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq 0(%rbx),%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	jne L.11
L.12:
	leaq QueensStaticOpen.diag2(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.4
L.21:
	movq %rax,%rcx
	movq %r14,%rax
	movabsq $8,%rbx
#	movq %rbx,%rbx
	subq $1,%rbx
	addq %rbx,%rax
#	movq %rax,%rax
	subq %r15,%rax
	movq %rax,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.15
L.22:
	movq 8(%rcx),%rax
	cmpq %rax,%rdx
	jge L.15
L.23:
	movq 0(%rcx),%rax
	movq %rax,%rbx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq 0(%rbx),%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	jne L.11
L.10:
	leaq QueensStaticOpen.row(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.4
L.24:
	movq %rax,%rbx
	movq %r14,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.15
L.25:
	movq 8(%rbx),%rax
	cmpq %rax,%rcx
	jge L.15
L.26:
	movq 0(%rbx),%rax
	movq %rax,%rbx
	movq %rcx,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq $1,0(%rbx)
	leaq QueensStaticOpen.diag1(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.4
L.27:
	movq %rax,%rbx
	movq %r14,%rax
	addq %r15,%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.15
L.28:
	movq 8(%rbx),%rax
	cmpq %rax,%rcx
	jge L.15
L.29:
	movq 0(%rbx),%rax
	movq %rax,%rbx
	movq %rcx,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq $1,0(%rbx)
	leaq QueensStaticOpen.diag2(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.4
L.30:
	movq %rax,%rcx
	movq %r14,%rax
	movabsq $8,%rbx
#	movq %rbx,%rbx
	subq $1,%rbx
	addq %rbx,%rax
#	movq %rax,%rax
	subq %r15,%rax
	movq %rax,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.15
L.31:
	movq 8(%rcx),%rax
	cmpq %rax,%rdx
	jge L.15
L.32:
	movq 0(%rcx),%rax
	movq %rax,%rbx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq $1,0(%rbx)
	leaq QueensStaticOpen.col(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.4
L.33:
	movq %rax,%rbx
	movq %r15,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.15
L.34:
	movq 8(%rbx),%rax
	cmpq %rax,%rcx
	jge L.15
L.35:
	movq 0(%rbx),%rax
	movq %rax,%rbx
	movq %rcx,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq %r14,0(%rbx)
	movq %r15,%rax
	addq $1,%rax
	movq %rax,%rdi
	call QueensStaticOpen.Solve
	leaq QueensStaticOpen.row(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.4
L.36:
	movq %rax,%rbx
	movq %r14,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.15
L.37:
	movq 8(%rbx),%rax
	cmpq %rax,%rcx
	jge L.15
L.38:
	movq 0(%rbx),%rax
	movq %rax,%rbx
	movq %rcx,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq $0,0(%rbx)
	leaq QueensStaticOpen.diag1(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.4
L.39:
	movq %rax,%rbx
	movq %r14,%rax
	addq %r15,%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.15
L.40:
	movq 8(%rbx),%rax
	cmpq %rax,%rcx
	jge L.15
L.41:
	movq 0(%rbx),%rax
	movq %rax,%rbx
	movq %rcx,%rax
	shlq $3,%rax
	addq %rax,%rbx
	movq $0,0(%rbx)
	leaq QueensStaticOpen.diag2(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.4
L.42:
	movq %rax,%rcx
	movq %r14,%rbx
	movabsq $8,%rax
#	movq %rax,%rax
	subq $1,%rax
	addq %rax,%rbx
	movq %rbx,%rax
	subq %r15,%rax
	movq %rax,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	jl L.15
L.43:
	movq 8(%rcx),%rax
	cmpq %rax,%rbx
	jge L.15
L.44:
	movq 0(%rcx),%rax
#	movq %rax,%rax
#	movq %rbx,%rbx
	shlq $3,%rbx
	addq %rbx,%rax
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
	subq $64,%rsp
	movq %r15, -56(%rbp)
	movq %r14, -48(%rbp)
	movq %r13, -40(%rbp)
	movq %r12, -32(%rbp)
	movq %rbx, -24(%rbp)
L.82:
	xorq %r14,%r14
	leaq QueensStaticOpen.col(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.46
L.47:
	movq 8(%rax),%rax
#	movq %rax,%rax
	subq $1,%rax
	movq %rax,%r12
	movq $1,%r13
L.49:
	cmpq %r12,%r14
	jle L.48
L.50:
	movabsq $10,%rax
	movq %rax,%rdi
	call putchar
	jmp L.45
L.48:
#	movq %r14,%r14
	xorq %rax,%rax
	movq %rax,-16(%rbp)
	leaq QueensStaticOpen.row(%rip),%rax
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.46
L.51:
	movq 8(%rax),%rax
#	movq %rax,%rax
	subq $1,%rax
	movq %rax,%rbx
	movq $1,%r15
L.53:
	movq -16(%rbp),%rax
#	movq %rax,%rax
	cmpq %rbx,%rax
	jle L.52
L.54:
	movabsq $10,%rax
	movq %rax,%rdi
	call putchar
	movq %r14,%rax
	addq %r13,%rax
	movq %rax,%r14
	jmp L.49
L.52:
	movq -16(%rbp),%rax
#	movq %rax,%rax
#	movq %rax,%rax
	movq %rax,-8(%rbp)
	movabsq $32,%rax
	movq %rax,%rdi
	call putchar
	leaq QueensStaticOpen.col(%rip),%rax
	movq 0(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.46
L.59:
	movq %rcx,%rdx
	movq %r14,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.55
L.60:
	movq 8(%rdx),%rax
	cmpq %rax,%rcx
	jge L.55
L.61:
	movq 0(%rdx),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rax
	movq -8(%rbp),%rcx
#	movq %rcx,%rcx
	cmpq %rcx,%rax
	je L.56
L.57:
	movabsq $46,%rax
	movq %rax,%rdi
	call putchar
L.58:
	movq -16(%rbp),%rax
#	movq %rax,%rax
#	movq %rax,%rax
	addq %r15,%rax
#	movq %rax,%rax
	movq %rax,-16(%rbp)
	jmp L.53
L.56:
	movabsq $81,%rax
	movq %rax,%rdi
	call putchar
	jmp L.58
L.45:
#	returnSink
	movq -24(%rbp),%rbx
	movq -32(%rbp),%r12
	movq -40(%rbp),%r13
	movq -48(%rbp),%r14
	movq -56(%rbp),%r15
	addq $64,%rsp
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
	movq %r12,%rax
	movq $16,%rbx
#	movq %rbx,%rbx
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rbx
#	movq %rbx,%rbx
	movq %rbx,%rdi
	call malloc
	movq %rax,%rsi
	movq %rsi,%rax
	addq $16,%rax
	movq %rax,0(%rsi)
	movq %r12,8(%rsi)
	movq 8(%rsi),%rax
	movq %rax,%rdi
	movq 0(%rsi),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rdi,%rbx
	jge L.66
L.64:
	movq %rax,%rcx
	movq %rbx,%rdx
	shlq $3,%rdx
	addq %rdx,%rcx
	movq $0,0(%rcx)
#	movq %rbx,%rbx
	addq $1,%rbx
#	movq %rbx,%rbx
L.65:
	cmpq %rdi,%rbx
	jl L.64
L.66:
	leaq QueensStaticOpen.row(%rip),%rax
	movq %rsi,0(%rax)
	movq $8,%r12
	xorq %rax,%rax
	cmpq %rax,%r12
	jl L.62
L.67:
	movq %r12,%rax
	movq $16,%rbx
#	movq %rbx,%rbx
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rbx
#	movq %rbx,%rbx
	movq %rbx,%rdi
	call malloc
	movq %rax,%rsi
	movq %rsi,%rax
	addq $16,%rax
	movq %rax,0(%rsi)
	movq %r12,8(%rsi)
	movq 8(%rsi),%rax
	movq %rax,%rdi
	movq 0(%rsi),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rdi,%rbx
	jge L.70
L.68:
	movq %rax,%rcx
	movq %rbx,%rdx
	shlq $3,%rdx
	addq %rdx,%rcx
	movq $0,0(%rcx)
#	movq %rbx,%rbx
	addq $1,%rbx
#	movq %rbx,%rbx
L.69:
	cmpq %rdi,%rbx
	jl L.68
L.70:
	leaq QueensStaticOpen.col(%rip),%rax
	movq %rsi,0(%rax)
	movabsq $8,%rax
#	movq %rax,%rax
	addq $8,%rax
#	movq %rax,%rax
	subq $1,%rax
	movq %rax,%r12
	xorq %rax,%rax
	cmpq %rax,%r12
	jl L.62
L.71:
	movq %r12,%rax
	movq $16,%rbx
#	movq %rbx,%rbx
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rbx
#	movq %rbx,%rbx
	movq %rbx,%rdi
	call malloc
	movq %rax,%rsi
	movq %rsi,%rax
	addq $16,%rax
	movq %rax,0(%rsi)
	movq %r12,8(%rsi)
	movq 8(%rsi),%rax
	movq %rax,%rdi
	movq 0(%rsi),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rdi,%rbx
	jge L.74
L.72:
	movq %rax,%rcx
	movq %rbx,%rdx
	shlq $3,%rdx
	addq %rdx,%rcx
	movq $0,0(%rcx)
#	movq %rbx,%rbx
	addq $1,%rbx
#	movq %rbx,%rbx
L.73:
	cmpq %rdi,%rbx
	jl L.72
L.74:
	leaq QueensStaticOpen.diag1(%rip),%rax
	movq %rsi,0(%rax)
	movabsq $8,%rax
#	movq %rax,%rax
	addq $8,%rax
#	movq %rax,%rax
	subq $1,%rax
	movq %rax,%r12
	xorq %rax,%rax
	cmpq %rax,%r12
	jl L.62
L.75:
	movq %r12,%rax
	movq $16,%rbx
#	movq %rbx,%rbx
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rbx
#	movq %rbx,%rbx
	movq %rbx,%rdi
	call malloc
	movq %rax,%rsi
	movq %rsi,%rax
	addq $16,%rax
	movq %rax,0(%rsi)
	movq %r12,8(%rsi)
	movq 8(%rsi),%rax
	movq %rax,%rdi
	movq 0(%rsi),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rdi,%rbx
	jge L.78
L.76:
	movq %rax,%rcx
	movq %rbx,%rdx
	shlq $3,%rdx
	addq %rdx,%rcx
	movq $0,0(%rcx)
#	movq %rbx,%rbx
	addq $1,%rbx
#	movq %rbx,%rbx
L.77:
	cmpq %rdi,%rbx
	jl L.76
L.78:
	leaq QueensStaticOpen.diag2(%rip),%rax
	movq %rsi,0(%rax)
	xorq %rax,%rax
	movq %rax,%rdi
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
	leaq L.79(%rip),%rax
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
L.80:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.86:
	leaq L.80(%rip),%rax
	movq %rax,%rdi
	call puts
	movabsq $1,%rax
	movq %rax,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
