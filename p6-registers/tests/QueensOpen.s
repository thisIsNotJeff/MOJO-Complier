	.data
	.balign 8
QueensOpen.Queens:
	.quad QueensOpen.Init
	.quad QueensOpen.Solve
	.text
QueensOpen.Init:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %r12, -16(%rbp)
	movq %rbx, -8(%rbp)
	movq %rdi,%rbx
L.103:
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.1
L.2:
	movq $8,%r12
	xorq %rax,%rax
	cmpq %rax,%r12
	jl L.3
L.4:
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
	movq %rax,%rcx
	addq $16,%rcx
	movq %rcx,0(%rax)
	movq %r12,8(%rax)
	movq 8(%rax),%rdx
#	movq %rdx,%rdx
	movq 0(%rax),%rcx
#	movq %rcx,%rcx
	xorq %rsi,%rsi
	cmpq %rdx,%rsi
	jge L.7
L.5:
	movq %rcx,%r8
	movq %rsi,%rdi
	shlq $3,%rdi
	addq %rdi,%r8
	movq $0,0(%r8)
#	movq %rsi,%rsi
	addq $1,%rsi
#	movq %rsi,%rsi
L.6:
	cmpq %rdx,%rsi
	jl L.5
L.7:
	movq %rax,0(%rbx)
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.1
L.8:
	movq $8,%r12
	xorq %rax,%rax
	cmpq %rax,%r12
	jl L.3
L.9:
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
	movq %rax,%rcx
	addq $16,%rcx
	movq %rcx,0(%rax)
	movq %r12,8(%rax)
	movq 8(%rax),%rdx
#	movq %rdx,%rdx
	movq 0(%rax),%rcx
#	movq %rcx,%rcx
	xorq %rsi,%rsi
	cmpq %rdx,%rsi
	jge L.12
L.10:
	movq %rcx,%r8
	movq %rsi,%rdi
	shlq $3,%rdi
	addq %rdi,%r8
	movq $0,0(%r8)
#	movq %rsi,%rsi
	addq $1,%rsi
#	movq %rsi,%rsi
L.11:
	cmpq %rdx,%rsi
	jl L.10
L.12:
	movq %rax,8(%rbx)
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.1
L.13:
	movabsq $8,%r12
#	movq %r12,%r12
	addq $8,%r12
#	movq %r12,%r12
	subq $1,%r12
#	movq %r12,%r12
	xorq %rax,%rax
	cmpq %rax,%r12
	jl L.3
L.14:
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
	movq %rax,%rcx
	addq $16,%rcx
	movq %rcx,0(%rax)
	movq %r12,8(%rax)
	movq 8(%rax),%rdx
#	movq %rdx,%rdx
	movq 0(%rax),%rcx
#	movq %rcx,%rcx
	xorq %rsi,%rsi
	cmpq %rdx,%rsi
	jge L.17
L.15:
	movq %rcx,%r8
	movq %rsi,%rdi
	shlq $3,%rdi
	addq %rdi,%r8
	movq $0,0(%r8)
#	movq %rsi,%rsi
	addq $1,%rsi
#	movq %rsi,%rsi
L.16:
	cmpq %rdx,%rsi
	jl L.15
L.17:
	movq %rax,16(%rbx)
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.1
L.18:
	movabsq $8,%r12
#	movq %r12,%r12
	addq $8,%r12
#	movq %r12,%r12
	subq $1,%r12
#	movq %r12,%r12
	xorq %rax,%rax
	cmpq %rax,%r12
	jl L.3
L.19:
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
	movq %rax,%rcx
	addq $16,%rcx
	movq %rcx,0(%rax)
	movq %r12,8(%rax)
	movq 8(%rax),%rdx
#	movq %rdx,%rdx
	movq 0(%rax),%rcx
#	movq %rcx,%rcx
	xorq %rsi,%rsi
	cmpq %rdx,%rsi
	jge L.22
L.20:
	movq %rcx,%r8
	movq %rsi,%rdi
	shlq $3,%rdi
	addq %rdi,%r8
	movq $0,0(%r8)
#	movq %rsi,%rsi
	addq $1,%rsi
#	movq %rsi,%rsi
L.21:
	cmpq %rdx,%rsi
	jl L.20
L.22:
	movq %rax,24(%rbx)
	movq %rbx,%rax
L.0:
#	returnSink
	movq -8(%rbp),%rbx
	movq -16(%rbp),%r12
	addq $16,%rsp
	popq %rbp
	ret
L.1:
	call badPtr
L.3:
	call badSub
	.text
QueensOpen.Solve:
	pushq %rbp
	movq %rsp,%rbp
	subq $48,%rsp
	movq %r15, -40(%rbp)
	movq %r14, -32(%rbp)
	movq %r13, -24(%rbp)
	movq %r12, -16(%rbp)
	movq %rbx, -8(%rbp)
	movq %rdi,%rbx
	movq %rsi,%r15
L.104:
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.27
L.28:
	movq 8(%rbx),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.29:
	movq 8(%rax),%rax
	cmpq %rax,%r15
	je L.24
L.25:
	xorq %r14,%r14
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.27
L.30:
	movq 0(%rbx),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.31:
	movq 8(%rax),%r12
#	movq %r12,%r12
	subq $1,%r12
#	movq %r12,%r12
	movq $1,%r13
L.33:
	cmpq %r12,%r14
	jle L.32
L.34:
L.26:
	jmp L.23
L.24:
	movq %rbx,%rdi
	call QueensOpen.Print
	jmp L.26
L.32:
#	movq %r14,%r14
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.27
L.39:
	movq 0(%rbx),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.40:
#	movq %rax,%rax
	movq %r14,%rcx
	xorq %rdx,%rdx
	cmpq %rdx,%rcx
	jl L.41
L.42:
	movq 8(%rax),%rdx
	cmpq %rdx,%rcx
	jge L.41
L.43:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.38
L.36:
#	movq %r14,%r14
	addq %r13,%r14
#	movq %r14,%r14
	jmp L.33
L.38:
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.27
L.44:
	movq 16(%rbx),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.45:
#	movq %rax,%rax
	movq %r14,%rcx
	addq %r15,%rcx
#	movq %rcx,%rcx
	xorq %rdx,%rdx
	cmpq %rdx,%rcx
	jl L.41
L.46:
	movq 8(%rax),%rdx
	cmpq %rdx,%rcx
	jge L.41
L.47:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jne L.36
L.37:
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.27
L.48:
	movq 24(%rbx),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.49:
#	movq %rax,%rax
	movq %r14,%rcx
	movabsq $8,%rdx
#	movq %rdx,%rdx
	subq $1,%rdx
	addq %rdx,%rcx
#	movq %rcx,%rcx
	subq %r15,%rcx
#	movq %rcx,%rcx
	xorq %rdx,%rdx
	cmpq %rdx,%rcx
	jl L.41
L.50:
	movq 8(%rax),%rdx
	cmpq %rdx,%rcx
	jge L.41
L.51:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jne L.36
L.35:
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.27
L.52:
	movq 0(%rbx),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.53:
#	movq %rax,%rax
	movq %r14,%rcx
	xorq %rdx,%rdx
	cmpq %rdx,%rcx
	jl L.41
L.54:
	movq 8(%rax),%rdx
	cmpq %rdx,%rcx
	jge L.41
L.55:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $1,0(%rax)
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.27
L.56:
	movq 16(%rbx),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.57:
#	movq %rax,%rax
	movq %r14,%rcx
	addq %r15,%rcx
#	movq %rcx,%rcx
	xorq %rdx,%rdx
	cmpq %rdx,%rcx
	jl L.41
L.58:
	movq 8(%rax),%rdx
	cmpq %rdx,%rcx
	jge L.41
L.59:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $1,0(%rax)
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.27
L.60:
	movq 24(%rbx),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.61:
#	movq %rax,%rax
	movq %r14,%rcx
	movabsq $8,%rdx
#	movq %rdx,%rdx
	subq $1,%rdx
	addq %rdx,%rcx
#	movq %rcx,%rcx
	subq %r15,%rcx
#	movq %rcx,%rcx
	xorq %rdx,%rdx
	cmpq %rdx,%rcx
	jl L.41
L.62:
	movq 8(%rax),%rdx
	cmpq %rdx,%rcx
	jge L.41
L.63:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $1,0(%rax)
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.27
L.64:
	movq 8(%rbx),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.65:
#	movq %rax,%rax
	movq %r15,%rcx
	xorq %rdx,%rdx
	cmpq %rdx,%rcx
	jl L.41
L.66:
	movq 8(%rax),%rdx
	cmpq %rdx,%rcx
	jge L.41
L.67:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq %r14,0(%rax)
	movq -8(%rbx),%rax
	movq 8(%rax),%rax
	movq %rbx,%rdi
	movq %r15,%rsi
	addq $1,%rsi
#	movq %rsi,%rsi
	call *%rax
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.27
L.68:
	movq 0(%rbx),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.69:
#	movq %rax,%rax
	movq %r14,%rcx
	xorq %rdx,%rdx
	cmpq %rdx,%rcx
	jl L.41
L.70:
	movq 8(%rax),%rdx
	cmpq %rdx,%rcx
	jge L.41
L.71:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $0,0(%rax)
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.27
L.72:
	movq 16(%rbx),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.73:
#	movq %rax,%rax
	movq %r14,%rcx
	addq %r15,%rcx
#	movq %rcx,%rcx
	xorq %rdx,%rdx
	cmpq %rdx,%rcx
	jl L.41
L.74:
	movq 8(%rax),%rdx
	cmpq %rdx,%rcx
	jge L.41
L.75:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $0,0(%rax)
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.27
L.76:
	movq 24(%rbx),%rcx
#	movq %rcx,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.77:
#	movq %rcx,%rcx
	movq %r14,%rax
	movabsq $8,%rdx
#	movq %rdx,%rdx
	subq $1,%rdx
	addq %rdx,%rax
#	movq %rax,%rax
	subq %r15,%rax
#	movq %rax,%rax
	xorq %rdx,%rdx
	cmpq %rdx,%rax
	jl L.41
L.78:
	movq 8(%rcx),%rdx
	cmpq %rdx,%rax
	jge L.41
L.79:
	movq 0(%rcx),%rcx
#	movq %rcx,%rcx
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq $0,0(%rcx)
	jmp L.36
L.23:
#	returnSink
	movq -8(%rbp),%rbx
	movq -16(%rbp),%r12
	movq -24(%rbp),%r13
	movq -32(%rbp),%r14
	movq -40(%rbp),%r15
	addq $48,%rsp
	popq %rbp
	ret
L.27:
	call badPtr
L.41:
	call badSub
	.text
QueensOpen.Print:
	pushq %rbp
	movq %rsp,%rbp
	subq $64,%rsp
	movq %r15, -56(%rbp)
	movq %r14, -48(%rbp)
	movq %r13, -40(%rbp)
	movq %r12, -32(%rbp)
	movq %rbx, -24(%rbp)
	movq %rdi,%r14
L.105:
	xorq %r12,%r12
	movq %r14,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.81
L.82:
	movq 8(%rax),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.81
L.83:
	movq 8(%rax),%rax
#	movq %rax,%rax
	subq $1,%rax
#	movq %rax,%rax
	movq %rax,-16(%rbp)
	movq $1,%rax
	movq %rax,-8(%rbp)
L.85:
	movq -16(%rbp),%rax
#	movq %rax,%rax
	cmpq %rax,%r12
	jle L.84
L.86:
	movabsq $10,%rdi
#	movq %rdi,%rdi
	call putchar
	jmp L.80
L.84:
#	movq %r12,%r12
	xorq %rbx,%rbx
	movq %r14,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.81
L.87:
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.81
L.88:
	movq 8(%rax),%rax
#	movq %rax,%rax
	subq $1,%rax
	movq %rax,%r15
	movq $1,%r13
L.90:
	cmpq %r15,%rbx
	jle L.89
L.91:
	movabsq $10,%rdi
#	movq %rdi,%rdi
	call putchar
	movq %r12,%rax
	movq -8(%rbp),%rbx
#	movq %rbx,%rbx
	addq %rbx,%rax
	movq %rax,%r12
	jmp L.85
L.89:
#	movq %rbx,%rbx
	movabsq $32,%rdi
#	movq %rdi,%rdi
	call putchar
	movq %r14,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.81
L.96:
	movq 8(%rax),%rax
#	movq %rax,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.81
L.97:
#	movq %rax,%rax
	movq %r12,%rcx
	xorq %rdx,%rdx
	cmpq %rdx,%rcx
	jl L.92
L.98:
	movq 8(%rax),%rdx
	cmpq %rdx,%rcx
	jge L.92
L.99:
	movq 0(%rax),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rax
	cmpq %rbx,%rax
	je L.93
L.94:
	movabsq $46,%rdi
#	movq %rdi,%rdi
	call putchar
L.95:
	movq %rbx,%rax
	addq %r13,%rax
	movq %rax,%rbx
	jmp L.90
L.93:
	movabsq $81,%rdi
#	movq %rdi,%rdi
	call putchar
	jmp L.95
L.80:
#	returnSink
	movq -24(%rbp),%rbx
	movq -32(%rbp),%r12
	movq -40(%rbp),%r13
	movq -48(%rbp),%r14
	movq -56(%rbp),%r15
	addq $64,%rsp
	popq %rbp
	ret
L.81:
	call badPtr
L.92:
	call badSub
	.text
QueensOpen:
	pushq %rbp
	movq %rsp,%rbp
	subq $16,%rsp
	movq %rbx, -8(%rbp)
L.106:
	movabsq $40,%rdi
#	movq %rdi,%rdi
	call malloc
#	movq %rax,%rax
#	movq %rax,%rax
	addq $8,%rax
#	movq %rax,%rax
	leaq QueensOpen.Queens(%rip),%rbx
	movq %rbx,-8(%rax)
	movq $0,0(%rax)
	movq $0,8(%rax)
	movq $0,16(%rax)
	movq $0,24(%rax)
#	movq %rax,%rax
	movq -8(%rax),%rbx
	movq 0(%rbx),%rbx
	movq %rax,%rdi
	call *%rbx
#	movq %rax,%rax
	movq -8(%rax),%rbx
	movq 8(%rbx),%rbx
	movq %rax,%rdi
	xorq %rsi,%rsi
#	movq %rsi,%rsi
	call *%rbx
#	returnSink
	movq -8(%rbp),%rbx
	addq $16,%rsp
	popq %rbp
	ret
L.100:
	call badSub
	.globl main
	.text
main:
	pushq %rbp
	movq %rsp,%rbp
L.107:
	call QueensOpen
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.101:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
	pushq %rbp
	movq %rsp,%rbp
L.108:
	leaq L.101(%rip),%rdi
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
L.102:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.109:
	leaq L.102(%rip),%rdi
#	movq %rdi,%rdi
	call puts
	movabsq $1,%rdi
#	movq %rdi,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
