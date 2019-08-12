
  Pod::Spec.new do |s|
    s.name = 'CapacitorContacts'
    s.version = '0.0.1'
    s.summary = 'contacts plugin'
    s.license = 'MIT'
    s.homepage = 'git@github.com:skrausler/capacitor-contacts.git'
    s.author = 'Stefan Krausler-Baumann'
    s.source = { :git => 'git@github.com:skrausler/capacitor-contacts.git', :tag => s.version.to_s }
    s.source_files = 'ios/Plugin/**/*.{swift,h,m,c,cc,mm,cpp}'
    s.ios.deployment_target  = '11.0'
    s.dependency 'Capacitor'
  end