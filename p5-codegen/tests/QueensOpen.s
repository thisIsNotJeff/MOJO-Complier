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
	movq %r12,%rdx
	movq $16,%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq %rcx,%rax
	movq %rax,%rdi
	call malloc
	movq %rax,%r8
	movq %r8,%rax
	addq $16,%rax
	movq %rax,0(%r8)
	movq %r12,8(%r8)
	movq 8(%r8),%rax
	movq %rax,%rdi
	movq 0(%r8),%rax
	movq %rax,%rsi
	xorq %rdx,%rdx
	cmpq %rdi,%rdx
	jge L.7
L.5:
	movq %rsi,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq $0,0(%rcx)
	movq %rdx,%rax
	addq $1,%rax
	movq %rax,%rdx
L.6:
	cmpq %rdi,%rdx
	jl L.5
L.7:
	movq %r8,0(%rbx)
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
	movq %r12,%rdx
	movq $16,%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq %rcx,%rax
	movq %rax,%rdi
	call malloc
	movq %rax,%r8
	movq %r8,%rax
	addq $16,%rax
	movq %rax,0(%r8)
	movq %r12,8(%r8)
	movq 8(%r8),%rax
	movq %rax,%rdi
	movq 0(%r8),%rax
	movq %rax,%rsi
	xorq %rdx,%rdx
	cmpq %rdi,%rdx
	jge L.12
L.10:
	movq %rsi,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq $0,0(%rcx)
	movq %rdx,%rax
	addq $1,%rax
	movq %rax,%rdx
L.11:
	cmpq %rdi,%rdx
	jl L.10
L.12:
	movq %r8,8(%rbx)
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
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
	movq %r12,%rdx
	movq $16,%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq %rcx,%rax
	movq %rax,%rdi
	call malloc
	movq %rax,%r8
	movq %r8,%rax
	addq $16,%rax
	movq %rax,0(%r8)
	movq %r12,8(%r8)
	movq 8(%r8),%rax
	movq %rax,%rdi
	movq 0(%r8),%rax
	movq %rax,%rsi
	xorq %rdx,%rdx
	cmpq %rdi,%rdx
	jge L.17
L.15:
	movq %rsi,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq $0,0(%rcx)
	movq %rdx,%rax
	addq $1,%rax
	movq %rax,%rdx
L.16:
	cmpq %rdi,%rdx
	jl L.15
L.17:
	movq %r8,16(%rbx)
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
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
	movq %r12,%rdx
	movq $16,%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq %rcx,%rax
	movq %rax,%rdi
	call malloc
	movq %rax,%r8
	movq %r8,%rax
	addq $16,%rax
	movq %rax,0(%r8)
	movq %r12,8(%r8)
	movq 8(%r8),%rax
	movq %rax,%rdi
	movq 0(%r8),%rax
	movq %rax,%rsi
	xorq %rdx,%rdx
	cmpq %rdi,%rdx
	jge L.22
L.20:
	movq %rsi,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq $0,0(%rcx)
	movq %rdx,%rax
	addq $1,%rax
	movq %rax,%rdx
L.21:
	cmpq %rdi,%rdx
	jl L.20
L.22:
	movq %r8,24(%rbx)
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
	movq %rdi,%r14
	movq %rsi,%r13
L.104:
	movq %r14,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.27
L.28:
	movq 8(%rbx),%rax
	movq %rax,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.27
L.29:
	movq 8(%rbx),%rax
	cmpq %rax,%r13
	je L.24
L.25:
	xorq %r15,%r15
	movq %r14,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.27
L.30:
	movq 0(%rbx),%rax
	movq %rax,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	je L.27
L.31:
	movq 8(%rbx),%rax
#	movq %rax,%rax
	subq $1,%rax
	movq %rax,%rbx
	movq $1,%r12
L.33:
	cmpq %rbx,%r15
	jle L.32
L.34:
L.26:
	jmp L.23
L.24:
	movq %r14,%rdi
	call QueensOpen.Print
	jmp L.26
L.32:
#	movq %r15,%r15
	movq %r14,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.39:
	movq 0(%rcx),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.40:
#	movq %rcx,%rcx
	movq %r15,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.41
L.42:
	movq 8(%rcx),%rax
	cmpq %rax,%rdx
	jge L.41
L.43:
	movq 0(%rcx),%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq 0(%rcx),%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.38
L.36:
	movq %r15,%rax
	addq %r12,%rax
	movq %rax,%r15
	jmp L.33
L.38:
	movq %r14,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.44:
	movq 16(%rcx),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.45:
#	movq %rcx,%rcx
	movq %r15,%rax
	addq %r13,%rax
	movq %rax,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.41
L.46:
	movq 8(%rcx),%rax
	cmpq %rax,%rdx
	jge L.41
L.47:
	movq 0(%rcx),%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq 0(%rcx),%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jne L.36
L.37:
	movq %r14,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.48:
	movq 24(%rcx),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.49:
	movq %rcx,%rsi
	movq %r15,%rcx
	movabsq $8,%rax
#	movq %rax,%rax
	subq $1,%rax
	addq %rax,%rcx
	movq %rcx,%rax
	subq %r13,%rax
	movq %rax,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.41
L.50:
	movq 8(%rsi),%rax
	cmpq %rax,%rdx
	jge L.41
L.51:
	movq 0(%rsi),%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq 0(%rcx),%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jne L.36
L.35:
	movq %r14,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.52:
	movq 0(%rcx),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.53:
#	movq %rcx,%rcx
	movq %r15,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.41
L.54:
	movq 8(%rcx),%rax
	cmpq %rax,%rdx
	jge L.41
L.55:
	movq 0(%rcx),%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq $1,0(%rcx)
	movq %r14,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.56:
	movq 16(%rcx),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.57:
#	movq %rcx,%rcx
	movq %r15,%rax
	addq %r13,%rax
	movq %rax,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.41
L.58:
	movq 8(%rcx),%rax
	cmpq %rax,%rdx
	jge L.41
L.59:
	movq 0(%rcx),%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq $1,0(%rcx)
	movq %r14,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.60:
	movq 24(%rcx),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.61:
	movq %rcx,%rsi
	movq %r15,%rcx
	movabsq $8,%rax
#	movq %rax,%rax
	subq $1,%rax
	addq %rax,%rcx
	movq %rcx,%rax
	subq %r13,%rax
	movq %rax,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.41
L.62:
	movq 8(%rsi),%rax
	cmpq %rax,%rdx
	jge L.41
L.63:
	movq 0(%rsi),%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq $1,0(%rcx)
	movq %r14,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.64:
	movq 8(%rcx),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.65:
#	movq %rcx,%rcx
	movq %r13,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.41
L.66:
	movq 8(%rcx),%rax
	cmpq %rax,%rdx
	jge L.41
L.67:
	movq 0(%rcx),%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq %r15,0(%rcx)
	movq -8(%r14),%rax
	movq 8(%rax),%rcx
	movq %r14,%rdi
	movq %r13,%rax
	addq $1,%rax
	movq %rax,%rsi
	call *%rcx
	movq %r14,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.68:
	movq 0(%rcx),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.69:
#	movq %rcx,%rcx
	movq %r15,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.41
L.70:
	movq 8(%rcx),%rax
	cmpq %rax,%rdx
	jge L.41
L.71:
	movq 0(%rcx),%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq $0,0(%rcx)
	movq %r14,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.72:
	movq 16(%rcx),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.73:
#	movq %rcx,%rcx
	movq %r15,%rax
	addq %r13,%rax
	movq %rax,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.41
L.74:
	movq 8(%rcx),%rax
	cmpq %rax,%rdx
	jge L.41
L.75:
	movq 0(%rcx),%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq $0,0(%rcx)
	movq %r14,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.76:
	movq 24(%rcx),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.27
L.77:
	movq %rcx,%rsi
	movq %r15,%rcx
	movabsq $8,%rax
#	movq %rax,%rax
	subq $1,%rax
	addq %rax,%rcx
	movq %rcx,%rax
	subq %r13,%rax
	movq %rax,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.41
L.78:
	movq 8(%rsi),%rax
	cmpq %rax,%rdx
	jge L.41
L.79:
	movq 0(%rsi),%rax
	movq %rax,%rcx
	movq %rdx,%rax
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
