        lw      0       1       mcand
        lw      0       2       mplier
        lw      0       3       pos1
        lw      0       4       exit
loop1   nand    2       3       5
        nand    5       5       5
        beq     5       0       loop2
        add     6       1       6
loop2   add     1       1       1
        add     3       3       3
        lw      0       7       neg1
        add     4       7       4
        beq     4       0       2
        beq     0       0       loop1
        noop
end     halt
mcand   .fill   105
mplier  .fill   32
pos1    .fill   1
neg1    .fill   -1
exit    .fill   15