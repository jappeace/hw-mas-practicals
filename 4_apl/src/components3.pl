% components3.pl

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
89).   
        
pc_case(antec, 
'Antec Performance II SX630II' ,
'Mini Tower', 
atx, 
300,
beige, 
pc_platform,  
36).

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
346).

pc_case(supermicro, 
'SuperMicro SC742',
'Tower',
no_atx,
420,
black, 
pc_platform,
540).

pc_case(enlight,    
'Enlight Server 8902',
'Tower',
atx,
300,
beige, 
pc_platform, 
217).



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
78). 

processor( intel,
'Intel Pentium 4',
1.8,
512, 
pc_platform,
147).

processor(intel,
'Intel Pentium III',
1.4,
512,
pc_platform,
1112).

processor(intel,
'Intel Xeon', 
2.4,
512,
pc_platform, 
266).

processor(hp,
'Hewlett Packard Xeon' , 
3.06,
512,
pc_platform,
849).

processor(hp,
'Hewlett Packard Xeon',  
2.8,
 512,
pc_platform,
512).

processor(hp,
'Hewlett Packard Pentium 4' , 
2, 
512,
pc_platform,
222).

processor(compaq,        
'Compaq Pentium III', 
1.4,
512,
pc_platform,
654).


        
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
321).

motherboard(asus, 
'Asustek A7N8X Deluxe Motherboard',
'NVidia nForce2 SPP', 
atx, 
['Athlon', 'Athlon XP', 'Duron'], 
12.02 * 9.65,
143).

motherboard(intel, 
'Intel Desktop Board D865GLC',  
'Intel 865G', 
micro_atx, 
['Celeron', 'Pentium 4'], 
9.61 * 9.61, 
129).



% graphic_card( 
%  Manufacturer,
%  Type,
%  Features: indicates type, or graphic processor
% Price Range).

graphic_card(sigma,
'Sigma Design REALmagic Xcard',
'MPEG Card', 
pci_interface,
107).

graphic_card(radeon, 
'ATI RADEON 7000 Graphics card',
'Graphics Card', 
has_processor('ATI RADEON 7000'),
49).

graphic_card('3dlabs',
'3Dlabs Oxygen GVX210graphics card',
'Multi-monitor Graphics Card',
has_processor('3Dlabs Glint R3 graphic processor'),
414).
