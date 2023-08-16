	.text
Compare.LT:
	pushq %rbp
	movq %rsp,%rbp
#	movq %rdi,%rdi
#	movq %rsi,%rsi
L.14:
	xorq %rax,%rax
	cmpq %rsi,%rdi
	jl L.1
L.2:
#	movq %rax,%rax
	jmp L.0
L.1:
	movq $1,%rax
	jmp L.2
L.0:
#	returnSink
	popq %rbp
	ret
	.text
Compare.LE:
	pushq %rbp
	movq %rsp,%rbp
#	movq %rdi,%rdi
#	movq %rsi,%rsi
L.15:
	xorq %rax,%rax
	cmpq %rsi,%rdi
	jle L.4
L.5:
#	movq %rax,%rax
	jmp L.3
L.4:
	movq $1,%rax
	jmp L.5
L.3:
#	returnSink
	popq %rbp
	ret
	.text
Compare.GT:
	pushq %rbp
	movq %rsp,%rbp
#	movq %rdi,%rdi
#	movq %rsi,%rsi
L.16:
	xorq %rax,%rax
	cmpq %rsi,%rdi
	jg L.7
L.8:
#	movq %rax,%rax
	jmp L.6
L.7:
	movq $1,%rax
	jmp L.8
L.6:
#	returnSink
	popq %rbp
	ret
	.text
Compare.GE:
	pushq %rbp
	movq %rsp,%rbp
#	movq %rdi,%rdi
#	movq %rsi,%rsi
L.17:
	xorq %rax,%rax
	cmpq %rsi,%rdi
	jge L.10
L.11:
#	movq %rax,%rax
	jmp L.9
L.10:
	movq $1,%rax
	jmp L.11
L.9:
#	returnSink
	popq %rbp
	ret
	.text
Compare:
	pushq %rbp
	movq %rsp,%rbp
#	returnSink
	popq %rbp
	ret
	.globl main
	.text
main:
	pushq %rbp
	movq %rsp,%rbp
L.18:
	call Compare
#	returnSink
	popq %rbp
	ret
	.data
	.balign 8
L.12:	.asciz	"Attempt to use a null pointer"
	.text
badPtr:
	pushq %rbp
	movq %rsp,%rbp
L.19:
	leaq L.12(%rip),%rdi
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
L.13:	.asciz	"Subscript out of bounds"
	.text
badSub:
	pushq %rbp
	movq %rsp,%rbp
L.20:
	leaq L.13(%rip),%rdi
#	movq %rdi,%rdi
	call puts
	movabsq $1,%rdi
#	movq %rdi,%rdi
	call exit
#	returnSink
	popq %rbp
	ret
