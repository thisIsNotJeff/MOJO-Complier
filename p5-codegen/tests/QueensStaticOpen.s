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
	subq $32,%rsp
	movq %r14, -32(%rbp)
	movq %r13, -24(%rbp)
	movq %r12, -16(%rbp)
	movq %rbx, -8(%rbp)
	movq %rdi,%rbx
L.81:
	leaq QueensStaticOpen.col(%rip),%rax
	movq 0(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.4
L.5:
	movq 8(%rcx),%rax
	cmpq %rax,%rbx
	je L.1
L.2:
	xorq %r14,%r14
	leaq QueensStaticOpen.row(%rip),%rax
	movq 0(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.4
L.6:
	movq 8(%rcx),%rax
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
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.4
L.14:
#	movq %rcx,%rcx
	movq %r14,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.15
L.16:
	movq 8(%rcx),%rax
	cmpq %rax,%rdx
	jge L.15
L.17:
	movq 0(%rcx),%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq 0(%rcx),%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.13
L.11:
	movq %r14,%rax
	addq %r13,%rax
	movq %rax,%r14
	jmp L.8
L.13:
	leaq QueensStaticOpen.diag1(%rip),%rax
	movq 0(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.4
L.18:
#	movq %rcx,%rcx
	movq %r14,%rax
	addq %rbx,%rax
	movq %rax,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.15
L.19:
	movq 8(%rcx),%rax
	cmpq %rax,%rdx
	jge L.15
L.20:
	movq 0(%rcx),%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq 0(%rcx),%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jne L.11
L.12:
	leaq QueensStaticOpen.diag2(%rip),%rax
	movq 0(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.4
L.21:
	movq %rcx,%rsi
	movq %r14,%rcx
	movabsq $8,%rax
#	movq %rax,%rax
	subq $1,%rax
	addq %rax,%rcx
	movq %rcx,%rax
	subq %rbx,%rax
	movq %rax,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.15
L.22:
	movq 8(%rsi),%rax
	cmpq %rax,%rdx
	jge L.15
L.23:
	movq 0(%rsi),%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq 0(%rcx),%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	jne L.11
L.10:
	leaq QueensStaticOpen.row(%rip),%rax
	movq 0(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.4
L.24:
#	movq %rcx,%rcx
	movq %r14,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.15
L.25:
	movq 8(%rcx),%rax
	cmpq %rax,%rdx
	jge L.15
L.26:
	movq 0(%rcx),%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq $1,0(%rcx)
	leaq QueensStaticOpen.diag1(%rip),%rax
	movq 0(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.4
L.27:
#	movq %rcx,%rcx
	movq %r14,%rax
	addq %rbx,%rax
	movq %rax,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.15
L.28:
	movq 8(%rcx),%rax
	cmpq %rax,%rdx
	jge L.15
L.29:
	movq 0(%rcx),%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq $1,0(%rcx)
	leaq QueensStaticOpen.diag2(%rip),%rax
	movq 0(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.4
L.30:
	movq %rcx,%rsi
	movq %r14,%rcx
	movabsq $8,%rax
#	movq %rax,%rax
	subq $1,%rax
	addq %rax,%rcx
	movq %rcx,%rax
	subq %rbx,%rax
	movq %rax,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.15
L.31:
	movq 8(%rsi),%rax
	cmpq %rax,%rdx
	jge L.15
L.32:
	movq 0(%rsi),%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq $1,0(%rcx)
	leaq QueensStaticOpen.col(%rip),%rax
	movq 0(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.4
L.33:
#	movq %rcx,%rcx
#	movq %rbx,%rbx
	xorq %rax,%rax
	cmpq %rax,%rbx
	jl L.15
L.34:
	movq 8(%rcx),%rax
	cmpq %rax,%rbx
	jge L.15
L.35:
	movq 0(%rcx),%rax
	movq %rax,%rcx
	movq %rbx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq %r14,0(%rcx)
	movq %rbx,%rax
	addq $1,%rax
	movq %rax,%rdi
	call QueensStaticOpen.Solve
	leaq QueensStaticOpen.row(%rip),%rax
	movq 0(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.4
L.36:
#	movq %rcx,%rcx
	movq %r14,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.15
L.37:
	movq 8(%rcx),%rax
	cmpq %rax,%rdx
	jge L.15
L.38:
	movq 0(%rcx),%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq $0,0(%rcx)
	leaq QueensStaticOpen.diag1(%rip),%rax
	movq 0(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.4
L.39:
#	movq %rcx,%rcx
	movq %r14,%rax
	addq %rbx,%rax
	movq %rax,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.15
L.40:
	movq 8(%rcx),%rax
	cmpq %rax,%rdx
	jge L.15
L.41:
	movq 0(%rcx),%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq $0,0(%rcx)
	leaq QueensStaticOpen.diag2(%rip),%rax
	movq 0(%rax),%rax
	movq %rax,%rcx
	xorq %rax,%rax
	cmpq %rax,%rcx
	je L.4
L.42:
	movq %rcx,%rsi
	movq %r14,%rcx
	movabsq $8,%rax
#	movq %rax,%rax
	subq $1,%rax
	addq %rax,%rcx
	movq %rcx,%rax
	subq %rbx,%rax
	movq %rax,%rdx
	xorq %rax,%rax
	cmpq %rax,%rdx
	jl L.15
L.43:
	movq 8(%rsi),%rax
	cmpq %rax,%rdx
	jge L.15
L.44:
	movq 0(%rsi),%rax
	movq %rax,%rcx
	movq %rdx,%rax
	shlq $3,%rax
	addq %rax,%rcx
	movq $0,0(%rcx)
	jmp L.11
L.0:
#	returnSink
	movq -8(%rbp),%rbx
	movq -16(%rbp),%r12
	movq -24(%rbp),%r13
	movq -32(%rbp),%r14
	addq $32,%rsp
	popq %rbp
	ret
L.4:
	call badPtr
L.15:
	call badSub
