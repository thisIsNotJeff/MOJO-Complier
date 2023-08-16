	.data
	.balign 8
Queens.Queens:
	.quad Queens.Init
	.quad Queens.Solve
	.text
Queens.Init:
	pushq %rbp
	movq %rsp,%rbp
	subq $32,%rsp
	movq %r13, -24(%rbp)
	movq %r12, -16(%rbp)
	movq %rbx, -8(%rbp)
	movq %rdi,%r13
L.64:
#	movq %r13,%r13
	cmpq $0,%r13
	je L.1
L.2:
	movabsq $64,%rax
	movq %rax,%rdi
	call malloc
	movq %rax,%rsi
	movabsq $8,%rax
	movq %rax,%rdx
	movabsq $0,%rax
	movq %rax,%rcx
	cmpq %rdx,%rcx
	jge L.4
L.3:
	shlq $3,%rcx
	addq %rcx, %rsi
	movabsq $0,%rax
	movq %rax,0(%rsi)
	addq $1,%rcx
#	movq %rcx,%rcx
	cmpq %rdx,%rcx
	jl L.3
L.4:
	movq %rsi,0(%r13)
#	movq %r13,%r13
	cmpq $0,%r13
	je L.1
L.5:
	movabsq $64,%rax
	movq %rax,%rdi
	call malloc
	movq %rax,%rsi
	movabsq $8,%rax
	movq %rax,%rdx
	movabsq $0,%rax
	movq %rax,%rcx
	cmpq %rdx,%rcx
	jge L.7
L.6:
	shlq $3,%rcx
	addq %rcx, %rsi
	movabsq $0,%rax
	movq %rax,0(%rsi)
	addq $1,%rcx
#	movq %rcx,%rcx
	cmpq %rdx,%rcx
	jl L.6
L.7:
	movq %rsi,8(%r13)
#	movq %r13,%r13
	cmpq $0,%r13
	je L.1
L.8:
	movabsq $120,%rax
	movq %rax,%rdi
	call malloc
	movq %rax,%rsi
	movq $1,%r12
	movabsq $8,%rax
	addq $8,%rax
	subq %r12, %rax
	movq %rax,%rdx
	movabsq $0,%rax
	movq %rax,%rcx
	cmpq %rdx,%rcx
	jge L.10
L.9:
	shlq $3,%rcx
	addq %rcx, %rsi
	movabsq $0,%rax
	movq %rax,0(%rsi)
	addq $1,%rcx
#	movq %rcx,%rcx
	cmpq %rdx,%rcx
	jl L.9
L.10:
	movq %rsi,16(%r13)
	movq %r13,%r12
	cmpq $0,%r12
	je L.1
L.11:
	movabsq $120,%rax
	movq %rax,%rdi
	call malloc
	movq %rax,%rdx
	movq $1,%rbx
	movabsq $8,%rax
	addq $8,%rax
	subq %rbx, %rax
	movq %rax,%rcx
	movabsq $0,%rax
	movq %rax,%rbx
	cmpq %rcx,%rbx
	jge L.13
L.12:
	shlq $3,%rbx
	addq %rbx, %rdx
	movabsq $0,%rax
	movq %rax,0(%rdx)
	addq $1,%rbx
#	movq %rbx,%rbx
	cmpq %rcx,%rbx
	jl L.12
L.13:
	movq %rdx,24(%r12)
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
