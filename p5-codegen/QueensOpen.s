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
	movq %r14, -32(%rbp)
	movq %r13, -24(%rbp)
	movq %r12, -16(%rbp)
	movq %rbx, -8(%rbp)
	movq %rdi,%r13
L.103:
#	movq %r13,%r13
	cmpq $0,%r13
	je L.1
L.2:
	movabsq $8,%rax
	movq %rax,%r14
	cmpq $0,%r14
	jl L.3
L.4:
	movq %r14,%rcx
	movabsq $16,%rax
#	movq %rax,%rax
	shlq $3,%rcx
	addq %rcx, %rax
#	movq %rax,%rax
	movq %rax,%rdi
	call malloc
	movq %rax,%rdi
	addq $16,%rdi
	movq %rdi,0(%rdi)
	movq %r14,8(%rdi)
	movq 8(%rdi),%rax
	movq %rax,%rsi
	movq 0(%rdi),%rax
	movq %rax,%rdx
	movabsq $0,%rax
	movq %rax,%rcx
	cmpq %rsi,%rcx
	jge L.7
L.5:
	shlq $3,%rcx
	addq %rcx, %rdx
	movabsq $0,%rax
	movq %rax,0(%rdx)
	addq $1,%rcx
#	movq %rcx,%rcx
L.6:
	cmpq %rsi,%rcx
	jl L.5
L.7:
	movq %rdi,0(%r13)
#	movq %r13,%r13
	cmpq $0,%r13
	je L.1
L.8:
	movabsq $8,%rax
	movq %rax,%r14
	cmpq $0,%r14
	jl L.3
L.9:
	movq %r14,%rcx
	movabsq $16,%rax
#	movq %rax,%rax
	shlq $3,%rcx
	addq %rcx, %rax
#	movq %rax,%rax
	movq %rax,%rdi
	call malloc
	movq %rax,%rdi
	addq $16,%rdi
	movq %rdi,0(%rdi)
	movq %r14,8(%rdi)
	movq 8(%rdi),%rax
	movq %rax,%rsi
	movq 0(%rdi),%rax
	movq %rax,%rdx
	movabsq $0,%rax
	movq %rax,%rcx
	cmpq %rsi,%rcx
	jge L.12
L.10:
	shlq $3,%rcx
	addq %rcx, %rdx
	movabsq $0,%rax
	movq %rax,0(%rdx)
	addq $1,%rcx
#	movq %rcx,%rcx
L.11:
	cmpq %rsi,%rcx
	jl L.10
L.12:
	movq %rdi,8(%r13)
#	movq %r13,%r13
	cmpq $0,%r13
	je L.1
L.13:
	movq $1,%r12
	movabsq $8,%rax
	addq $8,%rax
	subq %r12, %rax
	movq %rax,%r12
	cmpq $0,%r12
	jl L.3
L.14:
	movq %r12,%rcx
	movabsq $16,%rax
#	movq %rax,%rax
	shlq $3,%rcx
	addq %rcx, %rax
#	movq %rax,%rax
	movq %rax,%rdi
	call malloc
	movq %rax,%rdi
	addq $16,%rdi
	movq %rdi,0(%rdi)
	movq %r12,8(%rdi)
	movq 8(%rdi),%rax
	movq %rax,%rsi
	movq 0(%rdi),%rax
	movq %rax,%rdx
	movabsq $0,%rax
	movq %rax,%rcx
	cmpq %rsi,%rcx
	jge L.17
L.15:
	shlq $3,%rcx
	addq %rcx, %rdx
	movabsq $0,%rax
	movq %rax,0(%rdx)
	addq $1,%rcx
#	movq %rcx,%rcx
L.16:
	cmpq %rsi,%rcx
	jl L.15
L.17:
	movq %rdi,16(%r13)
	movq %r13,%r12
	cmpq $0,%r12
	je L.1
L.18:
	movq $1,%rbx
	movabsq $8,%rax
	addq $8,%rax
	subq %rbx, %rax
	movq %rax,%rbx
	cmpq $0,%rbx
	jl L.3
L.19:
	movq %rbx,%rcx
	movabsq $16,%rax
#	movq %rax,%rax
	shlq $3,%rcx
	addq %rcx, %rax
#	movq %rax,%rax
	movq %rax,%rdi
	call malloc
	movq %rax,%rsi
	addq $16,%rsi
	movq %rsi,0(%rsi)
	movq %rbx,8(%rsi)
	movq 8(%rsi),%rax
	movq %rax,%rdx
	movq 0(%rsi),%rax
	movq %rax,%rcx
	movabsq $0,%rax
	movq %rax,%rbx
	cmpq %rdx,%rbx
	jge L.22
L.20:
	shlq $3,%rbx
	addq %rbx, %rcx
	movabsq $0,%rax
	movq %rax,0(%rcx)
	addq $1,%rbx
#	movq %rbx,%rbx
L.21:
	cmpq %rdx,%rbx
	jl L.20
L.22:
	movq %rsi,24(%r12)
	movq %r13,%rax
L.0:
#	returnSink
	movq -8(%rbp),%rbx
	movq -16(%rbp),%r12
	movq -24(%rbp),%r13
	movq -32(%rbp),%r14
	addq $32,%rsp
	popq %rbp
	ret
L.1:
	call badPtr
L.3:
	call badSub
