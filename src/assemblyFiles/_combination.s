        lw  0   1   n
        lw  0   2   r
        lw  0   6   stack
        lw  0   7   rAddr

combi   beq 1   2   combi2
        beq 2   0   combi2
        lw  0   5   neg1
        add 1   5   1
        lw  0   5   pos1
        sw  6   7   stack
        add 6   5   6
        sw  6   1   stack
        add 6   5   6
        sw  6   2   stack
        add 6   5   6
        lw  0   5   comAdr
        jalr 5  7

        add 3   0   4
        lw  0   5   pos1
        sw  6   4   stack
        lw  0   5   neg1
        add 6   5   6
        lw  6   2   stack
        add 6   5   6
        lw  6   1   stack
        add 2   5   2
        lw  0   5   pos1
        add 6   5   6
        add 6   5   6
        add 6   5   6 
        lw  0   5   comAdr
        jalr 5  7
        
        lw  0   5   neg1
        add 6   5   6
        lw  6   4   stack
        add 3   4   3
        add 6   5   6
        add 6   5   6
        add 6   5   6
        lw  6   7   stack
        jalr 7  5
        beq 0   0   end

combi2  lw  0   5   pos1
        add 0   5   3
        jalr    7   5

end halt

n       .fill   7
r       .fill   3
pos1    .fill    1
neg1    .fill   -1
comAdr     .fill   combi
rAddr     .fill   end
stack   .fill   0