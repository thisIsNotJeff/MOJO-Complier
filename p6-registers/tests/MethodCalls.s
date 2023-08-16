	.data
	.balign 8
MethodCalls.A:
	.quad badPtr
	.data
	.balign 8
MethodCalls.AB:
	.quad badPtr
	.quad badPtr
	.data
	.balign 8
MethodCalls.T1:
	.quad badPtr
	.quad MethodCalls.Pab
	.data
	.balign 8
MethodCalls.T2:
	.quad MethodCalls.Pa
	.data
	.balign 8
MethodCalls.T3:
	.quad badPtr
	.quad MethodCalls.Pa
	.text
MethodCalls:
	pushq %rbp
	movq %rsp,%rbp
	subq $48,%rsp
	movq %r15, -40(%rbp)
	movq %r14, -32(%rbp)
	movq %r13, -24(%rbp)
	movq %r12, -16(%rbp)
	movq %rbx, -8(%rbp)
L.2:
	xorq %r15,%r15
	xorq %r14,%r14
	xorq %r13,%r13
	xorq %r12,%r12
	xorq %rbx,%rbx
	movq -8(%r15),%rax
	movq 0(%rax),%rax
	movq %r15,%rdi
	call *%rax
	leaq MethodCalls.A(%rip),%rax
	movq 0(%rax),%rax
	movq %r15,%rdi
	call *%rax
	movq -8(%r14),%rax
	movq 8(%rax),%rax
	movq %r14,%rdi
	call *%rax
	leaq MethodCalls.AB(%rip),%rax
	movq 8(%rax),%rax
	movq %r14,%rdi
	call *%rax
	movq -8(%r13),%rax
	movq 8(%rax),%rax
	movq %r13,%rdi
	call *%rax
	leaq MethodCalls.T1(%rip),%rax
	movq 8(%rax),%rax
	movq %r13,%rdi
	call *%rax
	movq -8(%r12),%rax
	movq 0(%rax),%rax
	movq %r12,%rdi
	call *%rax
	leaq MethodCalls.T2(%rip),%rax
	movq 0(%rax),%rax
	movq %r12,%rdi
	call *%rax
	movq -8(%rbx),%rax
	movq 8(%rax),%rax
	movq %rbx,%rdi
	call *%rax
	leaq MethodCalls.T3(%rip),%rax
	movq 8(%rax),%rax
	movq %rbx,%rdi
	call *%rax
#	returnSink
	movq -8(%rbp),%rbx
	movq -16(%rbp),%r12
	movq -24(%rbp),%r13
	movq -32(%rbp),%r14
	movq -40(%rbp),%r15
	addq $48,%rsp
	popq %rbp
	ret
	.globl main
	.text
main:
	pushq %rbp
	movq %rsp,%rbp
L.3:
	call MethodCalls
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.0:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
	pushq %rbp
	movq %rsp,%rbp
L.4:
	leaq L.0(%rip),%rdi
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
L.1:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.5:
	leaq L.1(%rip),%rdi
#	movq %rdi,%rdi
	call puts
	movabsq $1,%rdi
#	movq %rdi,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
