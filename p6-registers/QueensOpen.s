	.data
	.balign 8
QueensOpen.Queens:
	.quad QueensOpen.Init
	.quad QueensOpen.Solve
	.text
QueensOpen.Init:
	pushq %rbp
	movq %rsp,%rbp
	subq $32,%rsp
	movq %r13, -24(%rbp)
	movq %r12, -16(%rbp)
	movq %rbx, -8(%rbp)
	movq %rdi,%r13
L.103:
#	movq %r13,%r13
	xorq %rax,%rax
	cmpq %rax,%r13
	je L.1
L.2:
	movq $8,%r12
	xorq %rax,%rax
	cmpq %rax,%r12
	jl L.3
L.4:
	movq %r12,%rax
	movq $16,%rbx
#	movq %rbx,%rbx
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rbx
#	movq %rbx,%rbx
	movq %rbx,%rdi
	call malloc
	movq %rax,%rcx
	movq %rcx,%rax
	addq $16,%rax
	movq %rax,0(%rcx)
	movq %r12,8(%rcx)
	movq 8(%rcx),%rax
	movq %rax,%rdx
	movq 0(%rcx),%rax
	movq %rax,%rsi
	xorq %rdi,%rdi
	cmpq %rdx,%rdi
	jge L.7
L.5:
	movq %rsi,%rax
	movq %rdi,%rbx
	shlq $3,%rbx
	addq %rbx,%rax
	movq $0,0(%rax)
	movq %rdi,%rax
	addq $1,%rax
	movq %rax,%rdi
L.6:
	cmpq %rdx,%rdi
	jl L.5
L.7:
	movq %rcx,0(%r13)
#	movq %r13,%r13
	xorq %rax,%rax
	cmpq %rax,%r13
	je L.1
L.8:
	movq $8,%r12
	xorq %rax,%rax
	cmpq %rax,%r12
	jl L.3
L.9:
	movq %r12,%rax
	movq $16,%rbx
#	movq %rbx,%rbx
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rbx
#	movq %rbx,%rbx
	movq %rbx,%rdi
	call malloc
	movq %rax,%rcx
	movq %rcx,%rax
	addq $16,%rax
	movq %rax,0(%rcx)
	movq %r12,8(%rcx)
	movq 8(%rcx),%rax
	movq %rax,%rdx
	movq 0(%rcx),%rax
	movq %rax,%rsi
	xorq %rdi,%rdi
	cmpq %rdx,%rdi
	jge L.12
L.10:
	movq %rsi,%rax
	movq %rdi,%rbx
	shlq $3,%rbx
	addq %rbx,%rax
	movq $0,0(%rax)
	movq %rdi,%rax
	addq $1,%rax
	movq %rax,%rdi
L.11:
	cmpq %rdx,%rdi
	jl L.10
L.12:
	movq %rcx,8(%r13)
#	movq %r13,%r13
	xorq %rax,%rax
	cmpq %rax,%r13
	je L.1
L.13:
	movabsq $8,%rax
#	movq %rax,%rax
	addq $8,%rax
#	movq %rax,%rax
	subq $1,%rax
	movq %rax,%r12
	xorq %rax,%rax
	cmpq %rax,%r12
	jl L.3
L.14:
	movq %r12,%rax
	movq $16,%rbx
#	movq %rbx,%rbx
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rbx
#	movq %rbx,%rbx
	movq %rbx,%rdi
	call malloc
	movq %rax,%rcx
	movq %rcx,%rax
	addq $16,%rax
	movq %rax,0(%rcx)
	movq %r12,8(%rcx)
	movq 8(%rcx),%rax
	movq %rax,%rdx
	movq 0(%rcx),%rax
	movq %rax,%rsi
	xorq %rdi,%rdi
	cmpq %rdx,%rdi
	jge L.17
L.15:
	movq %rsi,%rax
	movq %rdi,%rbx
	shlq $3,%rbx
	addq %rbx,%rax
	movq $0,0(%rax)
	movq %rdi,%rax
	addq $1,%rax
	movq %rax,%rdi
L.16:
	cmpq %rdx,%rdi
	jl L.15
L.17:
	movq %rcx,16(%r13)
#	movq %r13,%r13
	xorq %rax,%rax
	cmpq %rax,%r13
	je L.1
L.18:
	movabsq $8,%rax
#	movq %rax,%rax
	addq $8,%rax
#	movq %rax,%rax
	subq $1,%rax
	movq %rax,%r12
	xorq %rax,%rax
	cmpq %rax,%r12
	jl L.3
L.19:
	movq %r12,%rax
	movq $16,%rbx
#	movq %rbx,%rbx
#	movq %rax,%rax
	shlq $3,%rax
	addq %rax,%rbx
#	movq %rbx,%rbx
	movq %rbx,%rdi
	call malloc
	movq %rax,%rcx
	movq %rcx,%rax
	addq $16,%rax
	movq %rax,0(%rcx)
	movq %r12,8(%rcx)
	movq 8(%rcx),%rax
	movq %rax,%rdx
	movq 0(%rcx),%rax
	movq %rax,%rsi
	xorq %rdi,%rdi
	cmpq %rdx,%rdi
	jge L.22
L.20:
	movq %rsi,%rax
	movq %rdi,%rbx
	shlq $3,%rbx
	addq %rbx,%rax
	movq $0,0(%rax)
	movq %rdi,%rax
	addq $1,%rax
	movq %rax,%rdi
L.21:
	cmpq %rdx,%rdi
	jl L.20
L.22:
	movq %rcx,24(%r13)
	movq %r13,%rax
L.0:
#	returnSink
	movq -8(%rbp),%rbx
	movq -16(%rbp),%r12
	movq -24(%rbp),%r13
	addq $32,%rsp
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
	movq %rdi,%r15
	movq %rsi,%r14
L.104:
	movq %r15,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.27
L.28:
	movq 8(%rax),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.27
L.29:
	movq 8(%rax),%rax
	cmpq %rax,%r14
	je L.24
L.25:
	xorq %r13,%r13
	movq %r15,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.27
L.30:
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.27
L.31:
	movq 8(%rax),%rax
#	movq %rax,%rax
	subq $1,%rax
	movq %rax,%rbx
	movq $1,%r12
L.33:
	cmpq %rbx,%r13
	jle L.32
L.34:
L.26:
	jmp L.23
L.24:
	movq %r15,%rdi
	call QueensOpen.Print
	jmp L.26
L.32:
#	movq %r13,%r13
	movq %r15,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.39:
	movq 0(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.40:
	movq %rcx,%rdx
	movq %r13,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.41
L.42:
	movq 8(%rdx),%rax
	cmpq %rax,%rcx
	jge L.41
L.43:
	movq 0(%rdx),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.38
L.36:
	movq %r13,%rax
	addq %r12,%rax
	movq %rax,%r13
	jmp L.33
L.38:
	movq %r15,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.44:
	movq 16(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.45:
	movq %rcx,%rdx
	movq %r13,%rax
	addq %r14,%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.41
L.46:
	movq 8(%rdx),%rax
	cmpq %rax,%rcx
	jge L.41
L.47:
	movq 0(%rdx),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	jne L.36
L.37:
	movq %r15,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.48:
	movq 24(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.49:
	movq %rcx,%rdx
	movq %r13,%rax
	movabsq $8,%rcx
#	movq %rcx,%rcx
	subq $1,%rcx
	addq %rcx,%rax
#	movq %rax,%rax
	subq %r14,%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.41
L.50:
	movq 8(%rdx),%rax
	cmpq %rax,%rcx
	jge L.41
L.51:
	movq 0(%rdx),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	jne L.36
L.35:
	movq %r15,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.52:
	movq 0(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.53:
	movq %rcx,%rdx
	movq %r13,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.41
L.54:
	movq 8(%rdx),%rax
	cmpq %rax,%rcx
	jge L.41
L.55:
	movq 0(%rdx),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $1,0(%rax)
	movq %r15,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.56:
	movq 16(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.57:
	movq %rcx,%rdx
	movq %r13,%rax
	addq %r14,%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.41
L.58:
	movq 8(%rdx),%rax
	cmpq %rax,%rcx
	jge L.41
L.59:
	movq 0(%rdx),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $1,0(%rax)
	movq %r15,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.60:
	movq 24(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.61:
	movq %rcx,%rdx
	movq %r13,%rax
	movabsq $8,%rcx
#	movq %rcx,%rcx
	subq $1,%rcx
	addq %rcx,%rax
#	movq %rax,%rax
	subq %r14,%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.41
L.62:
	movq 8(%rdx),%rax
	cmpq %rax,%rcx
	jge L.41
L.63:
	movq 0(%rdx),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $1,0(%rax)
	movq %r15,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.64:
	movq 8(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.65:
	movq %rcx,%rdx
	movq %r14,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.41
L.66:
	movq 8(%rdx),%rax
	cmpq %rax,%rcx
	jge L.41
L.67:
	movq 0(%rdx),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq %r13,0(%rax)
	movq -8(%r15),%rax
	movq 8(%rax),%rax
	movq %r15,%rdi
	movq %r14,%rcx
	addq $1,%rcx
	movq %rcx,%rsi
	call *%rax
	movq %r15,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.68:
	movq 0(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.69:
	movq %rcx,%rdx
	movq %r13,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.41
L.70:
	movq 8(%rdx),%rax
	cmpq %rax,%rcx
	jge L.41
L.71:
	movq 0(%rdx),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $0,0(%rax)
	movq %r15,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.72:
	movq 16(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.73:
	movq %rcx,%rdx
	movq %r13,%rax
	addq %r14,%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.41
L.74:
	movq 8(%rdx),%rax
	cmpq %rax,%rcx
	jge L.41
L.75:
	movq 0(%rdx),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq $0,0(%rax)
	movq %r15,%rax
	xorq %rcx,%rcx
	cmpq %rcx,%rax
	je L.27
L.76:
	movq 24(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.77:
	movq %rcx,%rdx
	movq %r13,%rax
	movabsq $8,%rcx
#	movq %rcx,%rcx
	subq $1,%rcx
	addq %rcx,%rax
#	movq %rax,%rax
	subq %r14,%rax
	movq %rax,%rsi
	xorq %rax,%rax
	cmpq %rax,%rsi
	jl L.41
L.78:
	movq 8(%rdx),%rax
	cmpq %rax,%rsi
	jge L.41
L.79:
	movq 0(%rdx),%rax
	movq %rax,%rcx
	movq %rsi,%rax
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
	movq %r15, -64(%rbp)
	movq %r14, -56(%rbp)
	movq %r13, -48(%rbp)
	movq %r12, -40(%rbp)
	movq %rbx, -32(%rbp)
	movq %rdi,%rax
	movq %rax,-16(%rbp)
L.105:
	xorq %r14,%r14
	movq -16(%rbp),%rax
#	movq %rax,%rax
#	movq %rax,%rax
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
	movq %rax,%r12
	movq $1,%r13
L.85:
	cmpq %r12,%r14
	jle L.84
L.86:
	movabsq $10,%rax
	movq %rax,%rdi
	call putchar
	jmp L.80
L.84:
#	movq %r14,%r14
	xorq %rax,%rax
	movq %rax,-24(%rbp)
	movq -16(%rbp),%rax
#	movq %rax,%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.81
L.87:
	movq 0(%rax),%rax
#	movq %rax,%rax
	xorq %rbx,%rbx
	cmpq %rbx,%rax
	je L.81
L.88:
	movq 8(%rax),%rax
#	movq %rax,%rax
	subq $1,%rax
	movq %rax,%rbx
	movq $1,%r15
L.90:
	movq -24(%rbp),%rax
#	movq %rax,%rax
	cmpq %rbx,%rax
	jle L.89
L.91:
	movabsq $10,%rax
	movq %rax,%rdi
	call putchar
	movq %r14,%rax
	addq %r13,%rax
	movq %rax,%r14
	jmp L.85
L.89:
	movq -24(%rbp),%rax
#	movq %rax,%rax
#	movq %rax,%rax
	movq %rax,-8(%rbp)
	movabsq $32,%rax
	movq %rax,%rdi
	call putchar
	movq -16(%rbp),%rax
#	movq %rax,%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.81
L.96:
	movq 8(%rcx),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.81
L.97:
	movq %rcx,%rdx
	movq %r14,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jl L.92
L.98:
	movq 8(%rdx),%rax
	cmpq %rax,%rcx
	jge L.92
L.99:
	movq 0(%rdx),%rax
#	movq %rax,%rax
#	movq %rcx,%rcx
	shlq $3,%rcx
	addq %rcx,%rax
	movq 0(%rax),%rax
	movq -8(%rbp),%rcx
#	movq %rcx,%rcx
	cmpq %rcx,%rax
	je L.93
L.94:
	movabsq $46,%rax
	movq %rax,%rdi
	call putchar
L.95:
	movq -24(%rbp),%rax
#	movq %rax,%rax
#	movq %rax,%rax
	addq %r15,%rax
#	movq %rax,%rax
	movq %rax,-24(%rbp)
	jmp L.90
L.93:
	movabsq $81,%rax
	movq %rax,%rdi
	call putchar
	jmp L.95
L.80:
#	returnSink
	movq -32(%rbp),%rbx
	movq -40(%rbp),%r12
	movq -48(%rbp),%r13
	movq -56(%rbp),%r14
	movq -64(%rbp),%r15
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
	movabsq $40,%rax
	movq %rax,%rdi
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
	xorq %rax,%rax
	movq %rax,%rsi
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
	leaq L.101(%rip),%rax
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
L.102:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.109:
	leaq L.102(%rip),%rax
	movq %rax,%rdi
	call puts
	movabsq $1,%rax
	movq %rax,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
