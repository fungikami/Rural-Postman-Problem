import os

insts = os.listdir('Instancias_RPP/URPP/')

for inst in insts:
    if not inst.startswith('._'):
        print(f'./runHeurRPP.sh v Instancias_RPP/URPP/{inst}')