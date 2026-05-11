-- =====================
-- UNIT MASTER
-- =====================
INSERT INTO unit_master (unit_name) VALUES

-- Weight
('Gram'),
('Kilogram'),
('Quintal'),
('Metric Ton'),
('Maund'),
('Seer'),

-- Volume
('Litre'),
('Millilitre'),

-- Count
('Piece'),
('Dozen'),
('Score'),
('Gross'),

-- Bundle / Pack
('Bundle'),
('Bag'),
('Box'),
('Crate'),
('Sack'),

-- Area Based
('Per Acre'),
('Per Hectare')
ON CONFLICT (unit_name) DO NOTHING;

-- =====================
-- PACKAGING MASTER
-- =====================
INSERT INTO packaging_master (packaging_type) VALUES

-- Bags
('Jute Bag'),
('HDPE Bag'),
('PP Woven Bag'),
('Gunny Bag'),
('Mesh Bag'),
('Net Bag'),
('Cotton Bag'),
('Paper Bag'),

-- Boxes & Crates
('Cardboard Box'),
('Wooden Crate'),
('Plastic Crate'),
('CFB Box'),

-- Bulk / Loose
('Loose / Bulk'),
('Tarpaulin Cover'),

-- Specialized
('Vacuum Pack'),
('Shrink Wrap'),
('Thermocol Box'),
('Bamboo Basket'),
('Plastic Drum'),
('Tin Container')
ON CONFLICT (packaging_type) DO NOTHING;

-- =====================
-- STORAGE MASTER
-- =====================
INSERT INTO storage_master (storage_type) VALUES

-- Open / Field Storage
('Open Field Storage'),
('Farm Yard Storage'),
('Threshing Floor Storage'),

-- Bag Storage
('Jute Bag Storage'),
('HDPE Bag Storage'),
('Poly Bag Storage'),

-- Warehouse Types
('General Warehouse'),
('Private Warehouse'),
('Government Warehouse'),
('CWC Warehouse'),('State Warehouse'),
('Bonded Warehouse'),
('Field Warehouse'),
('Cooperative Warehouse'),

-- Cold Storage Types
('Cold Storage - Fresh Fruits'),
('Cold Storage - Vegetables'),
('Cold Storage - Potato'),
('Cold Storage - Onion'),
('Cold Storage - Flowers'),
('Cold Storage - Dairy'),
('Cold Storage - Multi Commodity'),
('Controlled Atmosphere Storage'),
('Modified Atmosphere Storage'),

-- Silo & Grain Storage
('Steel Silo'),
('Concrete Silo'),
('Underground Silo'),
('Hopper Bottom Silo'),
('Flat Bottom Silo'),
('Hermetic Storage'),
('Pusa Bin'),
('Metal Bin'),
('Mud Bin'),

-- Pack House & Processing
('Pack House'),
('Pre-Cooling Unit'),
('Ripening Chamber'),
('Grading and Sorting Unit'),

-- Transport Storage
('Refrigerated Van'),
('Insulated Van'),
('Reefer Container'),

-- Traditional Storage
('Granary'),
('Kothi'),
('Bamboo Bin'),
('Clay Pot Storage'),
('Underground Pit Storage'),
('Bukhari'),

-- Shed / Covered Storage
('Covered Shed'),
('Tarpaulin Covered Storage'),
('Temporary Shed'),
('Permanent RCC Shed')
ON CONFLICT (storage_type) DO NOTHING;
-- =====================
-- STATE MASTER (28 States + 8 UTs)
-- =====================
INSERT INTO state_master (name) VALUES
('Andhra Pradesh'),        -- 1
('Arunachal Pradesh'),     -- 2
('Assam'),                 -- 3
('Bihar'),                 -- 4
('Chhattisgarh'),          -- 5
('Goa'),                   -- 6
('Gujarat'),               -- 7
('Haryana'),               -- 8
('Himachal Pradesh'),      -- 9
('Jharkhand'),             -- 10
('Karnataka'),             -- 11
('Kerala'),                -- 12
('Madhya Pradesh'),        -- 13
('Maharashtra'),           -- 14
('Manipur'),               -- 15
('Meghalaya'),             -- 16
('Mizoram'),               -- 17
('Nagaland'),              -- 18
('Odisha'),                -- 19
('Punjab'),                -- 20
('Rajasthan'),             -- 21
('Sikkim'),                -- 22
('Tamil Nadu'),            -- 23
('Telangana'),             -- 24
('Tripura'),               -- 25
('Uttar Pradesh'),         -- 26
('Uttarakhand'),           -- 27
('West Bengal'),           -- 28
('Andaman and Nicobar Islands'), -- 29
('Chandigarh'),            -- 30
('Dadra and Nagar Haveli and Daman and Diu'), -- 31
('Delhi'),                 -- 32
('Jammu and Kashmir'),     -- 33
('Ladakh'),                -- 34
('Lakshadweep'),           -- 35
('Puducherry')             -- 36
ON CONFLICT (name) DO NOTHING;

-- =====================
-- DISTRICT MASTER
-- =====================

-- Andhra Pradesh (state_id = 1)
INSERT INTO district_master (name, state_id) VALUES
('Alluri Sitharama Raju', 1),('Anakapalli', 1),('Ananthapuramu', 1),('Annamayya', 1),
('Bapatla', 1),('Chittoor', 1),('Dr. B.R. Ambedkar Konaseema', 1),('East Godavari', 1),
('Eluru', 1),('Guntur', 1),('Kakinada', 1),('Krishna', 1),
('Kurnool', 1),('Nandyal', 1),('NTR', 1),('Palnadu', 1),
('Parvathipuram Manyam', 1),('Prakasam', 1),('Sri Potti Sriramulu Nellore', 1),('Sri Sathya Sai', 1),
('Srikakulam', 1),('Tirupati', 1),('Visakhapatnam', 1),('Vizianagaram', 1),
('West Godavari', 1),('YSR Kadapa', 1)
ON CONFLICT (name,state_id) DO NOTHING;

-- Arunachal Pradesh (state_id = 2)
INSERT INTO district_master (name, state_id) VALUES
('Anjaw', 2),('Changlang', 2),('Dibang Valley', 2),('East Kameng', 2),
('East Siang', 2),('Kamle', 2),('Kra Daadi', 2),('Kurung Kumey', 2),
('Lepa Rada', 2),('Lohit', 2),('Longding', 2),('Lower Dibang Valley', 2),
('Lower Siang', 2),('Lower Subansiri', 2),('Namsai', 2),('Pakke Kessang', 2),
('Papum Pare', 2),('Shi Yomi', 2),('Siang', 2),('Tawang', 2),
('Tirap', 2),('Upper Siang', 2),('Upper Subansiri', 2),('West Kameng', 2),
('West Siang', 2)
ON CONFLICT (name,state_id) DO NOTHING;

-- Assam (state_id = 3)
INSERT INTO district_master (name, state_id) VALUES
('Bajali', 3),('Baksa', 3),('Barpeta', 3),('Biswanath', 3),
('Bongaigaon', 3),('Cachar', 3),('Charaideo', 3),('Chirang', 3),
('Darrang', 3),('Dhemaji', 3),('Dhubri', 3),('Dibrugarh', 3),
('Dima Hasao', 3),('Goalpara', 3),('Golaghat', 3),('Hailakandi', 3),
('Hojai', 3),('Jorhat', 3),('Kamrup', 3),('Kamrup Metropolitan', 3),
('Karbi Anglong', 3),('Karimganj', 3),('Kokrajhar', 3),('Lakhimpur', 3),
('Majuli', 3),('Morigaon', 3),('Nagaon', 3),('Nalbari', 3),
('Sivasagar', 3),('Sonitpur', 3),('South Salmara-Mankachar', 3),('Tinsukia', 3),
('Udalguri', 3),('West Karbi Anglong', 3)
ON CONFLICT (name,state_id) DO NOTHING;

-- Bihar (state_id = 4)
INSERT INTO district_master (name, state_id) VALUES
('Araria', 4),('Arwal', 4),('Aurangabad', 4),('Banka', 4),
('Begusarai', 4),('Bhagalpur', 4),('Bhojpur', 4),('Buxar', 4),
('Darbhanga', 4),('East Champaran', 4),('Gaya', 4),('Gopalganj', 4),
('Jamui', 4),('Jehanabad', 4),('Kaimur', 4),('Katihar', 4),
('Khagaria', 4),('Kishanganj', 4),('Lakhisarai', 4),('Madhepura', 4),
('Madhubani', 4),('Munger', 4),('Muzaffarpur', 4),('Nalanda', 4),
('Nawada', 4),('Patna', 4),('Purnia', 4),('Rohtas', 4),
('Saharsa', 4),('Samastipur', 4),('Saran', 4),('Sheikhpura', 4),
('Sheohar', 4),('Sitamarhi', 4),('Siwan', 4),('Supaul', 4),
('Vaishali', 4),('West Champaran', 4)
ON CONFLICT (name,state_id) DO NOTHING;

-- Chhattisgarh (state_id = 5)
INSERT INTO district_master (name, state_id) VALUES
('Balod', 5),('Baloda Bazar', 5),('Balrampur', 5),('Bastar', 5),
('Bemetara', 5),('Bijapur', 5),('Bilaspur', 5),('Dantewada', 5),
('Dhamtari', 5),('Durg', 5),('Gariaband', 5),('Gaurela-Pendra-Marwahi', 5),
('Janjgir-Champa', 5),('Jashpur', 5),('Kabirdham', 5),('Kanker', 5),
('Khairagarh', 5),('Kondagaon', 5),('Korba', 5),('Koriya', 5),
('Mahasamund', 5),('Manendragarh', 5),('Mohla-Manpur', 5),('Mungeli', 5),
('Narayanpur', 5),('Raigarh', 5),('Raipur', 5),('Rajnandgaon', 5),
('Sakti', 5),('Sarangarh-Bilaigarh', 5),('Sukma', 5),('Surajpur', 5),
('Surguja', 5)
ON CONFLICT (name,state_id) DO NOTHING;
-- Goa (state_id = 6)
INSERT INTO district_master (name, state_id) VALUES
('North Goa', 6),('South Goa', 6)
ON CONFLICT (name,state_id) DO NOTHING;

-- Gujarat (state_id = 7)
INSERT INTO district_master (name, state_id) VALUES
('Ahmedabad', 7),('Amreli', 7),('Anand', 7),('Aravalli', 7),
('Banaskantha', 7),('Bharuch', 7),('Bhavnagar', 7),('Botad', 7),
('Chhota Udaipur', 7),('Dahod', 7),('Dang', 7),('Devbhoomi Dwarka', 7),
('Gandhinagar', 7),('Gir Somnath', 7),('Jamnagar', 7),('Junagadh', 7),
('Kheda', 7),('Kutch', 7),('Mahisagar', 7),('Mehsana', 7),
('Morbi', 7),('Narmada', 7),('Navsari', 7),('Panchmahal', 7),
('Patan', 7),('Porbandar', 7),('Rajkot', 7),('Sabarkantha', 7),
('Surat', 7),('Surendranagar', 7),('Tapi', 7),('Vadodara', 7),
('Valsad', 7)
ON CONFLICT (name,state_id) DO NOTHING;

-- Haryana (state_id = 8)
INSERT INTO district_master (name, state_id) VALUES
('Ambala', 8),('Bhiwani', 8),('Charkhi Dadri', 8),('Faridabad', 8),
('Fatehabad', 8),('Gurugram', 8),('Hisar', 8),('Jhajjar', 8),
('Jind', 8),('Kaithal', 8),('Karnal', 8),('Kurukshetra', 8),
('Mahendragarh', 8),('Nuh', 8),('Palwal', 8),('Panchkula', 8),
('Panipat', 8),('Rewari', 8),('Rohtak', 8),('Sirsa', 8),
('Sonipat', 8),('Yamunanagar', 8)
ON CONFLICT (name,state_id) DO NOTHING;

-- Himachal Pradesh (state_id = 9)
INSERT INTO district_master (name, state_id) VALUES
('Bilaspur', 9),('Chamba', 9),('Hamirpur', 9),('Kangra', 9),
('Kinnaur', 9),('Kullu', 9),('Lahaul and Spiti', 9),('Mandi', 9),
('Shimla', 9),('Sirmaur', 9),('Solan', 9),('Una', 9)
ON CONFLICT (name,state_id) DO NOTHING;

-- Jharkhand (state_id = 10)
INSERT INTO district_master (name, state_id) VALUES
('Bokaro', 10),('Chatra', 10),('Deoghar', 10),('Dhanbad', 10),
('Dumka', 10),('East Singhbhum', 10),('Garhwa', 10),('Giridih', 10),
('Godda', 10),('Gumla', 10),('Hazaribagh', 10),('Jamtara', 10),
('Khunti', 10),('Koderma', 10),('Latehar', 10),('Lohardaga', 10),
('Pakur', 10),('Palamu', 10),('Ramgarh', 10),('Ranchi', 10),
('Sahebganj', 10),('Seraikela Kharsawan', 10),('Simdega', 10),('West Singhbhum', 10)
ON CONFLICT (name,state_id) DO NOTHING;

-- Karnataka (state_id = 11)
INSERT INTO district_master (name, state_id) VALUES
('Bagalkot', 11),('Ballari', 11),('Belagavi', 11),('Bengaluru Rural', 11),
('Bengaluru Urban', 11),('Bidar', 11),('Chamarajanagar', 11),('Chikkaballapur', 11),
('Chikkamagaluru', 11),('Chitradurga', 11),('Dakshina Kannada', 11),('Davanagere', 11),
('Dharwad', 11),('Gadag', 11),('Hassan', 11),('Haveri', 11),
('Kalaburagi', 11),('Kodagu', 11),('Kolar', 11),('Koppal', 11),
('Mandya', 11),('Mysuru', 11),('Raichur', 11),('Ramanagara', 11),
('Shivamogga', 11),('Tumakuru', 11),('Udupi', 11),('Uttara Kannada', 11),
('Vijayanagara', 11),('Vijayapura', 11),('Yadgir', 11)
ON CONFLICT (name,state_id) DO NOTHING;

-- Kerala (state_id = 12)
INSERT INTO district_master (name, state_id) VALUES
('Alappuzha', 12),('Ernakulam', 12),('Idukki', 12),('Kannur', 12),
('Kasaragod', 12),('Kollam', 12),('Kottayam', 12),('Kozhikode', 12),
('Malappuram', 12),('Palakkad', 12),('Pathanamthitta', 12),('Thiruvananthapuram', 12),
('Thrissur', 12),('Wayanad', 12)
ON CONFLICT (name,state_id) DO NOTHING;

-- Madhya Pradesh (state_id = 13)
INSERT INTO district_master (name, state_id) VALUES
('Agar Malwa', 13),('Alirajpur', 13),('Anuppur', 13),('Ashoknagar', 13),
('Balaghat', 13),('Barwani', 13),('Betul', 13),('Bhind', 13),
('Bhopal', 13),('Burhanpur', 13),('Chhatarpur', 13),('Chhindwara', 13),
('Damoh', 13),('Datia', 13),('Dewas', 13),('Dhar', 13),
('Dindori', 13),('Guna', 13),('Gwalior', 13),('Harda', 13),
('Hoshangabad', 13),('Indore', 13),('Jabalpur', 13),('Jhabua', 13),
('Katni', 13),('Khandwa', 13),('Khargone', 13),('Mandla', 13),
('Mandsaur', 13),('Morena', 13),('Narsinghpur', 13),('Neemuch', 13),
('Niwari', 13),('Panna', 13),('Raisen', 13),('Rajgarh', 13),
('Ratlam', 13),('Rewa', 13),('Sagar', 13),('Satna', 13),
('Sehore', 13),('Seoni', 13),('Shahdol', 13),('Shajapur', 13),
('Sheopur', 13),('Shivpuri', 13),('Sidhi', 13),('Singrauli', 13),
('Tikamgarh', 13),('Ujjain', 13),('Umaria', 13),('Vidisha', 13)
ON CONFLICT (name,state_id) DO NOTHING;

-- Maharashtra (state_id = 14)
INSERT INTO district_master (name, state_id) VALUES
('Ahmednagar', 14),('Akola', 14),('Amravati', 14),('Aurangabad', 14),
('Beed', 14),('Bhandara', 14),('Buldhana', 14),('Chandrapur', 14),
('Dhule', 14),('Gadchiroli', 14),('Gondia', 14),('Hingoli', 14),
('Jalgaon', 14),('Jalna', 14),('Kolhapur', 14),('Latur', 14),
('Mumbai City', 14),('Mumbai Suburban', 14),('Nagpur', 14),('Nanded', 14),
('Nandurbar', 14),('Nashik', 14),('Osmanabad', 14),('Palghar', 14),
('Parbhani', 14),('Pune', 14),('Raigad', 14),('Ratnagiri', 14),
('Sangli', 14),('Satara', 14),('Sindhudurg', 14),('Solapur', 14),
('Thane', 14),('Wardha', 14),('Washim', 14),('Yavatmal', 14)
ON CONFLICT (name,state_id) DO NOTHING;

-- Manipur (state_id = 15)
INSERT INTO district_master (name, state_id) VALUES
('Bishnupur', 15),('Chandel', 15),('Churachandpur', 15),('Imphal East', 15),
('Imphal West', 15),('Jiribam', 15),('Kakching', 15),('Kamjong', 15),
('Kangpokpi', 15),('Noney', 15),('Pherzawl', 15),('Senapati', 15),
('Tamenglong', 15),('Tengnoupal', 15),('Thoubal', 15),('Ukhrul', 15)
ON CONFLICT (name,state_id) DO NOTHING;

-- Meghalaya (state_id = 16)
INSERT INTO district_master (name, state_id) VALUES
('East Garo Hills', 16),('East Jaintia Hills', 16),('East Khasi Hills', 16),('Eastern West Khasi Hills', 16),
('North Garo Hills', 16),('Ri Bhoi', 16),('South Garo Hills', 16),('South West Garo Hills', 16),
('South West Khasi Hills', 16),('West Garo Hills', 16),('West Jaintia Hills', 16),('West Khasi Hills', 16)
ON CONFLICT (name,state_id) DO NOTHING;

-- Mizoram (state_id = 17)
INSERT INTO district_master (name, state_id) VALUES
('Aizawl', 17),('Champhai', 17),('Hnahthial', 17),('Khawzawl', 17),
('Kolasib', 17),('Lawngtlai', 17),('Lunglei', 17),('Mamit', 17),
('Saiha', 17),('Saitual', 17),('Serchhip', 17)
ON CONFLICT (name,state_id) DO NOTHING;

-- Nagaland (state_id = 18)
INSERT INTO district_master (name, state_id) VALUES
('Chumoukedima', 18),('Dimapur', 18),('Kiphire', 18),('Kohima', 18),
('Longleng', 18),('Mokokchung', 18),('Mon', 18),('Niuland', 18),
('Noklak', 18),('Peren', 18),('Phek', 18),('Shamator', 18),
('Tseminyu', 18),('Tuensang', 18),('Wokha', 18),('Zunheboto', 18)
ON CONFLICT (name,state_id) DO NOTHING;

-- Odisha (state_id = 19)
INSERT INTO district_master (name, state_id) VALUES
('Angul', 19),('Balangir', 19),('Balasore', 19),('Bargarh', 19),
('Bhadrak', 19),('Boudh', 19),('Cuttack', 19),('Deogarh', 19),
('Dhenkanal', 19),('Gajapati', 19),('Ganjam', 19),('Jagatsinghpur', 19),
('Jajpur', 19),('Jharsuguda', 19),('Kalahandi', 19),('Kandhamal', 19),
('Kendrapara', 19),('Kendujhar', 19),('Khordha', 19),('Koraput', 19),
('Malkangiri', 19),('Mayurbhanj', 19),('Nabarangpur', 19),('Nayagarh', 19),
('Nuapada', 19),('Puri', 19),('Rayagada', 19),('Sambalpur', 19),
('Subarnapur', 19),('Sundargarh', 19)
ON CONFLICT (name,state_id) DO NOTHING;

-- Punjab (state_id = 20)
INSERT INTO district_master (name, state_id) VALUES
('Amritsar', 20),('Barnala', 20),('Bathinda', 20),('Faridkot', 20),
('Fatehgarh Sahib', 20),('Fazilka', 20),('Ferozepur', 20),('Gurdaspur', 20),
('Hoshiarpur', 20),('Jalandhar', 20),('Kapurthala', 20),('Ludhiana', 20),
('Malerkotla', 20),('Mansa', 20),('Moga', 20),('Mohali', 20),
('Muktsar', 20),('Nawanshahr', 20),('Pathankot', 20),('Patiala', 20),
('Rupnagar', 20),('Sangrur', 20),('Tarn Taran', 20)
ON CONFLICT (name,state_id) DO NOTHING;

-- Rajasthan (state_id = 21)
INSERT INTO district_master (name, state_id) VALUES
('Ajmer', 21),('Alwar', 21),('Banswara', 21),('Baran', 21),
('Barmer', 21),('Bharatpur', 21),('Bhilwara', 21),('Bikaner', 21),
('Bundi', 21),('Chittorgarh', 21),('Churu', 21),('Dausa', 21),
('Dholpur', 21),('Dungarpur', 21),('Ganganagar', 21),('Hanumangarh', 21),
('Jaipur', 21),('Jaisalmer', 21),('Jalore', 21),('Jhalawar', 21),
('Jhunjhunu', 21),('Jodhpur', 21),('Karauli', 21),('Kota', 21),
('Nagaur', 21),('Pali', 21),('Pratapgarh', 21),('Rajsamand', 21),
('Sawai Madhopur', 21),('Sikar', 21),('Sirohi', 21),('Tonk', 21),
('Udaipur', 21)
ON CONFLICT (name,state_id) DO NOTHING;

-- Sikkim (state_id = 22)
INSERT INTO district_master (name, state_id) VALUES
('East Sikkim', 22),('North Sikkim', 22),('Pakyong', 22),('Soreng', 22),
('South Sikkim', 22),('West Sikkim', 22)
ON CONFLICT (name,state_id) DO NOTHING;

-- Tamil Nadu (state_id = 23)
INSERT INTO district_master (name, state_id) VALUES
('Ariyalur', 23),('Chengalpattu', 23),('Chennai', 23),('Coimbatore', 23),
('Cuddalore', 23),('Dharmapuri', 23),('Dindigul', 23),('Erode', 23),
('Kallakurichi', 23),('Kancheepuram', 23),('Karur', 23),('Krishnagiri', 23),
('Madurai', 23),('Mayiladuthurai', 23),('Nagapattinam', 23),('Namakkal', 23),
('Nilgiris', 23),('Perambalur', 23),('Pudukkottai', 23),('Ramanathapuram', 23),
('Ranipet', 23),('Salem', 23),('Sivaganga', 23),('Tenkasi', 23),
('Thanjavur', 23),('Theni', 23),('Thoothukudi', 23),('Tiruchirappalli', 23),
('Tirunelveli', 23),('Tirupathur', 23),('Tiruppur', 23),('Tiruvallur', 23),
('Tiruvannamalai', 23),('Tiruvarur', 23),('Vellore', 23),('Viluppuram', 23),
('Virudhunagar', 23)
ON CONFLICT (name,state_id) DO NOTHING;

-- Telangana (state_id = 24)
INSERT INTO district_master (name, state_id) VALUES
('Adilabad', 24),('Bhadradri Kothagudem', 24),('Hyderabad', 24),('Jagtial', 24),
('Jangaon', 24),('Jayashankar Bhupalpally', 24),('Jogulamba Gadwal', 24),('Kamareddy', 24),
('Karimnagar', 24),('Khammam', 24),('Kumuram Bheem', 24),('Mahabubabad', 24),
('Mahabubnagar', 24),('Mancherial', 24),('Medak', 24),('Medchal Malkajgiri', 24),
('Mulugu', 24),('Nagarkurnool', 24),('Nalgonda', 24),('Narayanpet', 24),
('Nirmal', 24),('Nizamabad', 24),('Peddapalli', 24),('Rajanna Sircilla', 24),
('Rangareddy', 24),('Sangareddy', 24),('Siddipet', 24),('Suryapet', 24),
('Vikarabad', 24),('Wanaparthy', 24),('Warangal Rural', 24),('Warangal Urban', 24),
('Yadadri Bhuvanagiri', 24)
ON CONFLICT (name,state_id) DO NOTHING;

-- Tripura (state_id = 25)
INSERT INTO district_master (name, state_id) VALUES
('Dhalai', 25),('Gomati', 25),('Khowai', 25),('North Tripura', 25),
('Sepahijala', 25),('South Tripura', 25),('Unakoti', 25),('West Tripura', 25)
ON CONFLICT (name,state_id) DO NOTHING;

-- Uttar Pradesh (state_id = 26)
INSERT INTO district_master (name, state_id) VALUES
('Agra', 26),('Aligarh', 26),('Ambedkar Nagar', 26),('Amethi', 26),
('Amroha', 26),('Auraiya', 26),('Ayodhya', 26),('Azamgarh', 26),
('Baghpat', 26),('Bahraich', 26),('Ballia', 26),('Balrampur', 26),
('Banda', 26),('Barabanki', 26),('Bareilly', 26),('Basti', 26),
('Bhadohi', 26),('Bijnor', 26),('Budaun', 26),('Bulandshahr', 26),
('Chandauli', 26),('Chitrakoot', 26),('Deoria', 26),('Etah', 26),
('Etawah', 26),('Farrukhabad', 26),('Fatehpur', 26),('Firozabad', 26),
('Gautam Buddha Nagar', 26),('Ghaziabad', 26),('Ghazipur', 26),('Gonda', 26),
('Gorakhpur', 26),('Hamirpur', 26),('Hapur', 26),('Hardoi', 26),
('Hathras', 26),('Jalaun', 26),('Jaunpur', 26),('Jhansi', 26),
('Kannauj', 26),('Kanpur Dehat', 26),('Kanpur Nagar', 26),('Kasganj', 26),
('Kaushambi', 26),('Kheri', 26),('Kushinagar', 26),('Lalitpur', 26),
('Lucknow', 26),('Maharajganj', 26),('Mahoba', 26),('Mainpuri', 26),
('Mathura', 26),('Mau', 26),('Meerut', 26),('Mirzapur', 26),
('Moradabad', 26),('Muzaffarnagar', 26),('Pilibhit', 26),('Pratapgarh', 26),
('Prayagraj', 26),('Raebareli', 26),('Rampur', 26),('Saharanpur', 26),
('Sambhal', 26),('Sant Kabir Nagar', 26),('Shahjahanpur', 26),('Shamli', 26),
('Shravasti', 26),('Siddharthnagar', 26),('Sitapur', 26),('Sonbhadra', 26),
('Sultanpur', 26),('Unnao', 26),('Varanasi', 26)
ON CONFLICT (name,state_id) DO NOTHING;

-- Uttarakhand (state_id = 27)
INSERT INTO district_master (name, state_id) VALUES
('Almora', 27),('Bageshwar', 27),('Chamoli', 27),('Champawat', 27),
('Dehradun', 27),('Haridwar', 27),('Nainital', 27),('Pauri Garhwal', 27),
('Pithoragarh', 27),('Rudraprayag', 27),('Tehri Garhwal', 27),('Udham Singh Nagar', 27),
('Uttarkashi', 27)
ON CONFLICT (name,state_id) DO NOTHING;

-- West Bengal (state_id = 28)
INSERT INTO district_master (name, state_id) VALUES
('Alipurduar', 28),('Bankura', 28),('Birbhum', 28),('Cooch Behar', 28),
('Dakshin Dinajpur', 28),('Darjeeling', 28),('Hooghly', 28),('Howrah', 28),
('Jalpaiguri', 28),('Jhargram', 28),('Kalimpong', 28),('Kolkata', 28),
('Malda', 28),('Murshidabad', 28),('Nadia', 28),('North 24 Parganas', 28),
('Paschim Bardhaman', 28),('Paschim Medinipur', 28),('Purba Bardhaman', 28),('Purba Medinipur', 28),
('Purulia', 28),('South 24 Parganas', 28),('Uttar Dinajpur', 28)
ON CONFLICT (name,state_id) DO NOTHING;

-- Andaman and Nicobar Islands (state_id = 29)
INSERT INTO district_master (name, state_id) VALUES
('Nicobar', 29),('North and Middle Andaman', 29),('South Andaman', 29)
ON CONFLICT (name,state_id) DO NOTHING;

-- Chandigarh (state_id = 30)
INSERT INTO district_master (name, state_id) VALUES
('Chandigarh', 30)
ON CONFLICT (name,state_id) DO NOTHING;

-- Dadra and Nagar Haveli and Daman and Diu (state_id = 31)
INSERT INTO district_master (name, state_id) VALUES
('Dadra and Nagar Haveli', 31),('Daman', 31),('Diu', 31)
ON CONFLICT (name,state_id) DO NOTHING;

-- Delhi (state_id = 32)
INSERT INTO district_master (name, state_id) VALUES
('Central Delhi', 32),('East Delhi', 32),('New Delhi', 32),('North Delhi', 32),
('North East Delhi', 32),('North West Delhi', 32),('Shahdara', 32),('South Delhi', 32),
('South East Delhi', 32),('South West Delhi', 32),('West Delhi', 32)
ON CONFLICT (name,state_id) DO NOTHING;

-- Jammu and Kashmir (state_id = 33)
INSERT INTO district_master (name, state_id) VALUES
('Anantnag', 33),('Bandipora', 33),('Baramulla', 33),('Budgam', 33),
('Doda', 33),('Ganderbal', 33),('Jammu', 33),('Kathua', 33),
('Kishtwar', 33),('Kulgam', 33),('Kupwara', 33),('Poonch', 33),
('Pulwama', 33),('Rajouri', 33),('Ramban', 33),('Reasi', 33),
('Samba', 33),('Shopian', 33),('Srinagar', 33),('Udhampur', 33)
ON CONFLICT (name,state_id) DO NOTHING;

-- Ladakh (state_id = 34)
INSERT INTO district_master (name, state_id) VALUES
('Kargil', 34),('Leh', 34)
ON CONFLICT (name,state_id) DO NOTHING;

-- Lakshadweep (state_id = 35)
INSERT INTO district_master (name, state_id) VALUES
('Lakshadweep', 35)
ON CONFLICT (name,state_id) DO NOTHING;

-- Puducherry (state_id = 36)
INSERT INTO district_master (name, state_id) VALUES
('Karaikal', 36),('Mahe', 36),('Puducherry', 36),('Yanam', 36)
ON CONFLICT (name,state_id) DO NOTHING;

-- =====================
-- CROP MASTER
-- =====================
-- Cereals & Millets
INSERT INTO crop_master (crop_name) VALUES
('Wheat'),('Rice'),('Maize'),('Bajra'),('Jowar'),
('Barley'),('Ragi'),('Oats'),('Quinoa'),('Buckwheat'),
('Foxtail Millet'),('Kodo Millet'),('Little Millet'),('Proso Millet'),('Barnyard Millet')
ON CONFLICT (crop_name) DO NOTHING;

-- Pulses & Legumes
INSERT INTO crop_master (crop_name) VALUES
('Chickpea'),('Lentil'),('Moong Dal'),('Urad Dal'),('Toor Dal'),
('Masoor Dal'),('Moth Bean'),('Horse Gram'),('Cowpea'),('Rajma'),
('Soybean'),('Field Peas'),('Lobia'),('Kulthi'),('Chana Dal')
ON CONFLICT (crop_name) DO NOTHING;

-- Oilseeds
INSERT INTO crop_master (crop_name) VALUES
('Groundnut'),('Mustard'),('Sunflower'),('Sesame'),('Linseed'),
('Safflower'),('Castor'),('Cottonseed'),('Niger Seed'),('Rapeseed')
ON CONFLICT (crop_name) DO NOTHING;

-- Vegetables
INSERT INTO crop_master (crop_name) VALUES
('Potato'),('Tomato'),('Onion'),('Garlic'),('Ginger'),
('Chilli'),('Capsicum'),('Brinjal'),('Okra'),('Cabbage'),
('Cauliflower'),('Broccoli'),('Spinach'),('Fenugreek Leaves'),('Coriander Leaves'),
('Mint'),('Curry Leaves'),('Drumstick'),('Bitter Gourd'),('Bottle Gourd'),
('Ridge Gourd'),('Snake Gourd'),('Ash Gourd'),('Pumpkin'),('Tinda'),
('Pointed Gourd'),('Cluster Beans'),('Broad Beans'),('French Beans'),('Peas'),
('Carrot'),('Radish'),('Turnip'),('Beetroot'),('Sweet Potato'),
('Colocasia'),('Yam'),('Tapioca'),('Elephant Foot Yam'),('Raw Banana'),
('Cucumber'),('Zucchini'),('Amaranth'),('Bathua'),('Knol Khol'),
('Lettuce'),('Celery'),('Asparagus'),('Leek'),('Spring Onion')
ON CONFLICT (crop_name) DO NOTHING;

-- Fruits
INSERT INTO crop_master (crop_name) VALUES
('Mango'),('Banana'),('Papaya'),('Guava'),('Pomegranate'),
('Grapes'),('Watermelon'),('Muskmelon'),('Pineapple'),('Jackfruit'),
('Litchi'),('Coconut'),('Sapota'),('Custard Apple'),('Fig'),
('Jamun'),('Ber'),('Amla'),('Tamarind'),('Bael'),
('Wood Apple'),('Mulberry'),('Strawberry'),('Passion Fruit'),('Dragon Fruit'),
('Kiwi'),('Avocado'),('Orange'),('Lemon'),('Lime'),
('Grapefruit'),('Mosambi'),('Mandarin'),('Pear'),('Apple'),
('Plum'),('Peach'),('Apricot'),('Cherry'),('Walnut')
ON CONFLICT (crop_name) DO NOTHING;

-- Spices & Condiments
INSERT INTO crop_master (crop_name) VALUES
('Turmeric'),('Cumin'),('Coriander Seed'),('Fennel'),('Fenugreek Seed'),
('Ajwain'),('Kalonji'),('Black Pepper'),('Cardamom'),('Clove'),
('Cinnamon'),('Nutmeg'),('Mace'),('Star Anise'),('Bay Leaf'),
('Dry Chilli'),('Paprika'),('Saffron'),('Vanilla'),('Asafoetida'),
('Mustard Seed'),('Poppy Seed'),('Dry Ginger'),('Long Pepper'),('Curry Leaf Dry')
ON CONFLICT (crop_name) DO NOTHING;

-- Cash Crops
INSERT INTO crop_master (crop_name) VALUES
('Cotton'),('Sugarcane'),('Jute'),('Tobacco'),('Rubber'),
('Coffee'),('Tea'),('Cocoa'),('Areca Nut'),('Betel Leaf')
ON CONFLICT (crop_name) DO NOTHING;

-- Flowers
INSERT INTO crop_master (crop_name) VALUES
('Rose'),('Marigold'),('Jasmine'),('Tuberose'),('Chrysanthemum'),
('Lotus'),('Sunflower Flower'),('Gerbera'),('Carnation'),('Gladiolus')
ON CONFLICT (crop_name) DO NOTHING;

-- Plantation & Others
INSERT INTO crop_master (crop_name) VALUES
('Bamboo'),('Moringa'),('Neem'),('Eucalyptus'),('Teak'),
('Lemongrass'),('Aloe Vera'),('Stevia'),('Ashwagandha'),('Brahmi'),
('Tulsi'),('Shatavari'),('Giloy'),('Kalmegh'),('Mulethi')
ON CONFLICT (crop_name) DO NOTHING;

INSERT INTO terms_and_policies
(
    terms_and_policies_id,
    terms_of_service,
    privacy_policy,
    cookie_policy,
    refund_policy,
    community_guidelines
)
VALUES
(
    1,
    'Welcome to KisanSetu. By accessing or using this platform, you agree to comply with the following terms and conditions. KisanSetu provides a digital marketplace connecting farmers and buyers for agricultural produce trading. Users must ensure that the information they provide during registration and listing creation is accurate and up to date. Users agree not to misuse the platform, including posting false listings, engaging in fraudulent bidding, or violating any applicable laws. KisanSetu reserves the right to suspend or terminate accounts that violate platform policies. All transactions conducted through the platform are the responsibility of the involved parties. KisanSetu acts only as a facilitator and is not responsible for disputes between buyers and sellers.',

    'KisanSetu values your privacy and is committed to protecting your personal information. We collect information such as name, contact details, location, and account activity to provide and improve our services. This information is used to facilitate marketplace transactions, improve user experience, and ensure platform security. Your personal information will not be sold or shared with third parties without your consent, except when required by law or necessary to provide platform services. Users are responsible for maintaining the confidentiality of their account credentials.',

    'KisanSetu uses cookies to enhance user experience and improve platform performance. Cookies are small data files stored on your device that help us remember your preferences, maintain secure sessions, and analyze platform usage. We use cookies for authentication, session management, and analytics purposes. Users can disable cookies through their browser settings, but some platform features may not function properly.',

    'KisanSetu acts as a marketplace connecting farmers and buyers. Payments and transactions may involve third-party payment providers. Refunds, if applicable, depend on the agreement between the buyer and seller. KisanSetu is not responsible for disputes related to product quality, delivery, or pricing. In case of technical issues or duplicate transactions involving platform service fees, refunds may be considered after review.',

    'KisanSetu aims to maintain a respectful and trustworthy marketplace. Users must ensure that listings accurately represent the agricultural products being sold, including quantity, quality, and pricing details. Fraudulent listings, bid manipulation, harassment, or attempts to bypass the platform for transactions are strictly prohibited. Violations may result in account suspension or permanent removal from the platform.'
)
ON CONFLICT (terms_and_policies_id) DO NOTHING;

INSERT INTO fraud_type_master (type_name) VALUES

('Identity Fraud'),
('Fake Listing'),
('Payment Scam'),
('Duplicate Listings'),
('Price Manipulation'),
('Fake Documents'),
('Spam or Misleading Info'),
('Unauthorized Reselling'),
('Account Misuse'),
('Other')

ON CONFLICT (type_name) DO NOTHING;

INSERT INTO report_reason_master (reason_name) VALUES
('Poor product quality'),
('Late or no delivery'),
('Product not as described'),
('Payment issues'),
('Poor communication'),
('Suspected fraud'),
('Other')
ON CONFLICT (reason_name) DO NOTHING;

INSERT INTO report_reason_master_Buyer (reason_name) VALUES
('Payment not completed'),
('Delayed payment'),
('Refused to accept delivery'),
('Cancelled order after confirmation'),
('Negotiation abuse / price manipulation'),
('Unresponsive / poor communication'),
('Provided incorrect delivery details'),
('Requested off-platform transaction'),
('Suspected fraud'),
('Other')
ON CONFLICT (reason_name) DO NOTHING;

INSERT INTO schemes (
    scheme_id,
    scheme_title,
    scheme_full_name,
    scheme_category,
    scheme_description,
    scheme_eligibility,
    scheme_state,
    scheme_official_link,
    scheme_last_updated_date
) VALUES
(1, 'PM-KISAN', 'Pradhan Mantri Kisan Samman Nidhi', 'Financial Support', 'Provides income support of Rs. 6000 per year to eligible farmer families.', 'All landholding farmer families', 'All India', 'https://pmkisan.gov.in', CURRENT_TIMESTAMP),
(2, 'PMFBY', 'Pradhan Mantri Fasal Bima Yojana', 'Insurance', 'Crop insurance scheme providing financial protection against crop loss.', 'Farmers growing notified crops', 'All India', 'https://pmfby.gov.in', CURRENT_TIMESTAMP),
(3, 'KCC', 'Kisan Credit Card', 'Credit', 'Provides credit support to farmers for agricultural needs.', 'Farmers with cultivable land', 'All India', 'https://www.myscheme.gov.in/schemes/kcc', CURRENT_TIMESTAMP),
(4, 'Soil Health Card', 'Soil Health Card Scheme', 'Agriculture Support', 'Provides soil testing and fertilizer recommendations.', 'All farmers', 'All India', 'https://soilhealth.dac.gov.in', CURRENT_TIMESTAMP),
(5, 'PMKSY', 'Pradhan Mantri Krishi Sinchai Yojana', 'Irrigation', 'Improves irrigation facilities and water efficiency.', 'All farmers', 'All India', 'https://pmksy.gov.in', CURRENT_TIMESTAMP),
(6, 'eNAM', 'National Agriculture Market', 'Market', 'Online trading platform for agricultural commodities.', 'Farmers and traders', 'All India', 'https://enam.gov.in', CURRENT_TIMESTAMP),
(7, 'PM KUSUM', 'Pradhan Mantri Kisan Urja Suraksha Yojana', 'Solar Subsidy', 'Provides solar energy solutions for farmers.', 'Farmers with agricultural land', 'All India', 'https://pmkusum.mnre.gov.in', CURRENT_TIMESTAMP),
(8, 'NABARD Subsidy', 'NABARD Farm Infrastructure Subsidy', 'Subsidy', 'Financial support for farm infrastructure development.', 'Farmers and FPOs', 'All India', 'https://www.nabard.org', CURRENT_TIMESTAMP),
(9, 'Gujarat Tractor Subsidy', 'Tractor Assistance Scheme Gujarat', 'Subsidy', 'Subsidy for purchasing tractors and equipment.', 'Farmers in Gujarat', 'Gujarat', 'https://ikhedut.gujarat.gov.in', CURRENT_TIMESTAMP),
(10, 'Gujarat Drip Irrigation', 'Micro Irrigation Scheme Gujarat', 'Irrigation', 'Supports drip irrigation systems.', 'Farmers in Gujarat', 'Gujarat', 'https://ikhedut.gujarat.gov.in', CURRENT_TIMESTAMP)
ON CONFLICT (scheme_id) DO UPDATE SET
    scheme_title = EXCLUDED.scheme_title,
    scheme_full_name = EXCLUDED.scheme_full_name,
    scheme_category = EXCLUDED.scheme_category,
    scheme_description = EXCLUDED.scheme_description,
    scheme_eligibility = EXCLUDED.scheme_eligibility,
    scheme_state = EXCLUDED.scheme_state,
    scheme_official_link = EXCLUDED.scheme_official_link,
    scheme_last_updated_date = EXCLUDED.scheme_last_updated_date;

DELETE FROM scheme_benefits WHERE scheme_id BETWEEN 1 AND 10;

INSERT INTO scheme_benefits (scheme_id, benefit) VALUES
(1, 'Rs. 6000 per year direct bank transfer'),
(1, 'Rs. 2000 per installment'),
(1, 'Support for small and marginal farmers'),
(2, 'Low premium rates'),
(2, 'Coverage for crop damage'),
(2, 'Fast claim settlement'),
(3, 'Low interest loans'),
(3, 'Flexible repayment'),
(3, 'Insurance included'),
(4, 'Free soil testing'),
(4, 'Better fertilizer usage'),
(4, 'Improved crop yield'),
(5, 'Subsidy on irrigation equipment'),
(5, 'Water conservation'),
(5, 'Micro irrigation support'),
(6, 'Better price discovery'),
(6, 'Online mandi access'),
(6, 'Direct selling'),
(7, 'Solar pump subsidy'),
(7, 'Reduced electricity cost'),
(7, 'Extra income via solar energy'),
(8, 'Warehouse subsidy'),
(8, 'Cold storage support'),
(8, 'Infrastructure funding'),
(9, 'Tractor subsidy'),
(9, 'Farm mechanization support'),
(10, 'Up to 70% subsidy'),
(10, 'Water saving'),
(10, 'Better crop yield');

SELECT setval(
    pg_get_serial_sequence('schemes', 'scheme_id'),
    GREATEST((SELECT COALESCE(MAX(scheme_id), 1) FROM schemes), 1)
);
