% components2.pl

% This file contains (part of) a description of computer hardware components, 


% -- simple component hierarchy ----------------------------------------
% can be replaced with something more realistic
%
%                                 pc                         
%                 ________________|__________________
%                 |         |           |            |
%             pc_case   processor   motherboard   graphic_card

component(pc, pc_case). 
component(pc, processor). 
component(pc, motherboard).
component(pc, graphic_card).

% -- product database -------------------------------------------------------------------

% pc_case( 
%   Manufacturer, 
%   Type, 
%   Shape, 
%   ATX, 
%   Power (watt), 
%   Color, 
%   Suitable For, 
%   Price). 

pc_case(antec, 
'Antec Cases SONATA Mid Tower Case',
'Mid Tower',
atx, 
'380', 
black,
pc_platform, 
91).   
        
pc_case(antec, 
'Antec Performance II SX630II' ,
'Mini Tower', 
atx, 
300,
beige, 
pc_platform,  
174).

pc_case(antec,  
'Antec Performance II SX835II',
'Tower', 
atx, 
350,
beige, 
pc_platform,  
155).

pc_case(supermicro, 
'SuperMicro SC822',
'Rack-Mountable',
no_atx,
400,
white,
pc_platform,
436).

pc_case(supermicro, 
'SuperMicro SC742',
'Tower',
no_atx,
420,
black, 
pc_platform,
234).

pc_case(enlight,    
'Enlight Server 8902',
'Tower',
atx,
300,
beige, 
pc_platform, 
359).



% processor( 
%   Manufacturer, 
%   Type, 
%   Speed (GHz), 
%   Cache Memory (Kb), 
%   Suitable For, 
%   Price Range ($) ). 


processor( intel, 
'Intel Celeron A' , 
2.0 ,
128, 
pc_platform,  
105). 

processor( intel,
'Intel Pentium 4',
1.8,
512, 
pc_platform,
179).

processor(intel,
'Intel Pentium III',
1.4,
512,
pc_platform,
1000).

processor(intel,
'Intel Xeon', 
2.4,
512,
pc_platform, 
310).

processor(hp,
'Hewlett Packard Xeon' , 
3.06,
512,
pc_platform,
999).

processor(hp,
'Hewlett Packard Xeon',  
2.8,
 512,
pc_platform,
895).

processor(hp,
'Hewlett Packard Pentium 4' , 
2, 
512,
pc_platform,
199).

processor(compaq,        
'Compaq Pentium III', 
1.4,
512,
pc_platform,
570).


        
% motherboard( 
%  Manufacturer, 
%  Type, 
%  Chipset, 
%  atx/no atx, 
%  Suitable For, 
%  Size (Inches), 
%  Price ($))       

motherboard(intel, 
'Intel Desktop Board D865PERL',  
'Intel 865PE', 
atx, 
['Celeron', 'Pentium 4'], 
12.02 * 9.61, 
205).

motherboard(asus, 
'Asustek A7N8X Deluxe Motherboard',
'NVidia nForce2 SPP', 
atx, 
['Athlon', 'Athlon XP', 'Duron'], 
12.02 * 9.65,
89).

motherboard(intel, 
'Intel Desktop Board D865GLC',  
'Intel 865G', 
micro_atx, 
['Celeron', 'Pentium 4'], 
9.61 * 9.61, 
95).



% graphic_card( 
%  Manufacturer,
%  Type,
%  Features: indicates type, or graphic processor
% Price Range).

graphic_card(sigma,
'Sigma Design REALmagic Xcard',
'MPEG Card', 
pci_interface,
99).

graphic_card(radeon, 
'ATI RADEON 7000 Graphics card',
'Graphics Card', 
has_processor('ATI RADEON 7000'),
45).

graphic_card('3dlabs',
'3Dlabs Oxygen GVX210graphics card',
'Multi-monitor Graphics Card',
has_processor('3Dlabs Glint R3 graphic processor'),
273).
